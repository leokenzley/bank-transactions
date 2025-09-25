package com.leokenzley.bank_transactions.service.impl;

import com.leokenzley.bank_transactions.model.request.TransactionCreditRequest;
import com.leokenzley.bank_transactions.model.request.TransactionTransferRequest;
import com.leokenzley.bank_transactions.model.request.TransactionalDebitRequest;
import com.leokenzley.bank_transactions.repository.AccountRepository;
import com.leokenzley.bank_transactions.service.TransactionService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {
  @Autowired
  private AccountRepository accountRepository;

  /**
   * Realiza um crédito na conta especificada.
   * Neste cenário, não é necessário o bloqueio pessimista,
   * pois o crédito não depende do saldo atual da conta.
   * Então não adiciono o lock na consulta.
   * @param credit
   */
  @Override
  public void credit(TransactionCreditRequest credit) {
    var account = accountRepository
      .findById(credit.accountId())
      .orElseThrow(()-> new RuntimeException("Conta não encontrada"));
    log.info("Credito atual do cliente {}: {}", account.getClientName(), account.getBalance());
    account.setBalance(account.getBalance().add(credit.amount()));
    log.info("Creditando {} na conta do cliente {}", credit.amount(), account.getClientName());
    var accountCredited = accountRepository.save(account);
    log.info("Novo saldo co cliente {}: R${}", accountCredited.getClientName(), accountCredited.getBalance());
  }

  /**
   * Realiza um débito na conta especificada.
   * Aqui é necessário o bloqueio pessimista, pois o débito depende do saldo atual
   * da conta que está sendo debitada.
   * Então adiciono o lock na consulta assim não corre o risco do saldo ser diferente do que
   * já está rodando no processamento.
   * @param request
   */
  @Transactional
  @Override
  public void debit(TransactionalDebitRequest request) {
    var account = accountRepository
      .findByAccountIdPessimistic(request.accountId())
      .orElseThrow(()-> new RuntimeException("Conta não encontrada"));

    log.info("Saldo atual do cliente {}: R$ {}", account.getClientName(), account.getBalance());
    log.info("Debitando R$ {} na conta do cliente {}", request.amount(), account.getClientName());

    if (account.getBalance().compareTo(request.amount()) < 0) {
      throw new RuntimeException("Saldo insuficiente");
    }
    account.setBalance(account.getBalance().subtract(request.amount()));
    var accountDebited = accountRepository.save(account);
    log.info("Novo saldo do cliente {}: R$ {}", accountDebited.getClientName(), accountDebited.getBalance());
  }

  /**
   * Realiza uma transferência entre duas contas.
   * Aqui é necessário o bloqueio pessimista, pois o débito depende do saldo atual
   * da conta que está sendo debitada.
   * Então adiciono o lock na consulta.
   *
   * 1. Um cenário para cobrir nos testes seria o de transferências concorrentes,
   * onde duas ou mais transferências são iniciadas ao mesmo tempo, e tentam debitar
   * da mesma conta. Com o lock pessimista, uma das transações irá esperar a outra
   * finalizar antes de prosseguir, garantindo que o saldo não fique negativo.
   *
   * 2. Transferencias para a mesma conta de destino não precisam de lock pessimista,
   * Acredito que não vai causa deadlock pois vai estar apontando para o mesmo objeto.
   * @param request
   */
  @Transactional
  @Override
  public void transfer(TransactionTransferRequest request) {
    // Aqui eu garanto que a ordem de bloqueio será sempre a mesma, será na ordem crescente de accountId
    // Segundo minha pesquisa, o banco de dados gerencia os locks, então não preciso me preocupar com deadlocks
    Long first = Math.min(request.fromAccountId(), request.toAccountId());
    Long second = Math.max(request.fromAccountId(), request.toAccountId());

    // Busco as duas contas com lock pessimista, assim tenho os as duas contas bloqueadas para alteração
    // enquanto vou debitar de uma conta, e creditar na outra
    var firstAccount = accountRepository.findByAccountIdPessimistic(first).orElseThrow();
    var secondAccount = accountRepository.findByAccountIdPessimistic(second).orElseThrow();

    // Aqui eu determino qual conta é a de débito e qual é a de crédito comparando os IDs
    var fromAccount = (firstAccount.getId().equals(request.fromAccountId())) ? firstAccount : secondAccount;
    var toAccount = (fromAccount == firstAccount) ? secondAccount : firstAccount;

    // Se a conta que for debitada tentar tirar um valor maior do que o saldo, lanço uma exceção
    if (fromAccount.getBalance().compareTo(request.amount()) < 0) throw new RuntimeException("Saldo insuficiente");

    // Atualizo o saldo das contas
    log.info("Transferindo R$ {} da conta {} para a conta {}", fromAccount.getClientName(), toAccount.getClientName());
    fromAccount.setBalance(fromAccount.getBalance().subtract(request.amount()));
    toAccount.setBalance(toAccount.getBalance().add(request.amount()));

    // Salvo as duas contas, e desbloqueio os registros no banco
    accountRepository.save(fromAccount);
    accountRepository.save(toAccount);
  }
}
