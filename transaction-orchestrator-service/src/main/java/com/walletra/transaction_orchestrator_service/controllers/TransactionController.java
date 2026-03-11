package com.walletra.transaction_orchestrator_service.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.walletra.events.TransactionRequest;
import com.walletra.transaction_orchestrator_service.services.TransactionService;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {
	
	private TransactionService transactionService;
	
	public TransactionController(TransactionService transactionService) {
		this.transactionService = transactionService;
	}
	
	@PostMapping("/create")
	public ResponseEntity<String> createTransaction(@RequestBody TransactionRequest transaction){
		return ResponseEntity.ok(transactionService.createTransaction(transaction));
	}

}
