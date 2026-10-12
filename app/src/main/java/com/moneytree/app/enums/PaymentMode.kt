package com.moneytree.app.enums

import com.moneytree.app.repository.network.requests.PaymentAmount

enum class WalletTypeMode(val value: String) {
	MEMBER("Member"),
	SELF("Self");
	
	companion object {
		fun fromValue(value: String): WalletTypeMode? {
			return entries.find { it.value == value }
		}
	}
}

fun getWalletTypeList(isOnlySelf: Boolean = true): MutableList<String> {
	val statusList: MutableList<String> = arrayListOf()
	if (isOnlySelf) {
		statusList.add(WalletTypeMode.SELF.value)
	} else {
		statusList.add(WalletTypeMode.MEMBER.value)
		statusList.add(WalletTypeMode.SELF.value)
	}
	return statusList
}

enum class PaymentMode(val value: String) {
	WALLET("Wallet"),
	CREDIT("Ov Credit"),
	CASH("Cash");
	
	companion object {
		fun fromValue(value: String): PaymentMode? {
			return entries.find { it.value == value }
		}
	}
}

enum class PaymentStatus(val value: String) {
	ALL("All"),
	PENDING("Pending"),
	ACCEPTED("Accepted"),
	REJECTED("Rejected");
	
	companion object {
		fun fromValue(value: String): PaymentStatus? {
			return entries.find { it.value == value }
		}
	}
}

enum class TransactionType(val value: String) {
	ALL("All"),
	CREDIT("Credit"),
	DEBIT("Debit");
	
	companion object {
		fun fromValue(value: String): TransactionType? {
			return entries.find { it.value == value }
		}
	}
}

fun getTransactionTypeList(): MutableList<String> {
	val statusList: MutableList<String> = arrayListOf()
	statusList.add(TransactionType.ALL.value)
	statusList.add(TransactionType.CREDIT.value)
	statusList.add(TransactionType.DEBIT.value)
	return statusList
}

enum class RegisterType(val value: String) {
	ALL("All"),
	ACTIVE("Active"),
	INACTIVE("InActive"),
	ACTIVATION("Activation"),
	UPGRADATION("Upgradation");
	
	companion object {
		fun fromValue(value: String): RegisterType? {
			return entries.find { it.value == value }
		}
	}
}

fun getRegisterTypeList(): MutableList<String> {
	val statusList: MutableList<String> = arrayListOf()
	statusList.add(RegisterType.ALL.value)
	statusList.add(RegisterType.ACTIVE.value)
	statusList.add(RegisterType.INACTIVE.value)
	statusList.add(RegisterType.ACTIVATION.value)
	statusList.add(RegisterType.UPGRADATION.value)
	return statusList
}


fun getPaymentStatusList(): MutableList<String> {
	val statusList: MutableList<String> = arrayListOf()
	statusList.add(PaymentStatus.ALL.value)
	statusList.add(PaymentStatus.PENDING.value)
	statusList.add(PaymentStatus.ACCEPTED.value)
	statusList.add(PaymentStatus.REJECTED.value)
	return statusList
}

fun getPaymentModeList(): MutableList<String> {
	val paymentModeList: MutableList<String> = arrayListOf()
	paymentModeList.add("Select Payment Mode")
	paymentModeList.add(PaymentMode.WALLET.value)
	paymentModeList.add(PaymentMode.CREDIT.value)
	paymentModeList.add(PaymentMode.CASH.value)
	return paymentModeList
}

fun calculatePaymentAmount(
	productAmount: Int,
	selectedType: PaymentMode,
	walletBalance: Int = 0,
	creditBalance: Int = 0
): PaymentAmount {
	
	var remainingAmount = productAmount
	
	var walletAmount = 0
	var creditAmount = 0
	var cashAmount = 0
	
	when (selectedType) {
		
		PaymentMode.WALLET -> {
			walletAmount = minOf(walletBalance, remainingAmount)
			remainingAmount -= walletAmount
			cashAmount = remainingAmount
		}
		
		PaymentMode.CREDIT -> {
			creditAmount = minOf(creditBalance, remainingAmount)
			remainingAmount -= creditAmount
			cashAmount = remainingAmount
		}
		
		PaymentMode.CASH -> {
			cashAmount = remainingAmount
		}
	}
	
	return PaymentAmount(
		walletAmount = walletAmount,
		creditAmount = creditAmount,
		cashAmount = cashAmount
	)
}

enum class SummaryType(val value: String) {
	All("All"),
	Pending("Pending"),
	Released("Released");
	
	companion object {
		fun fromValue(value: String): SummaryType? {
			return entries.find { it.value == value }
		}
	}
}

fun getSummaryTypeList(): MutableList<String> {
	val list: MutableList<String> = arrayListOf()
	list.add(SummaryType.All.value)
	list.add(SummaryType.Pending.value)
	list.add(SummaryType.Released.value)
	return list
}