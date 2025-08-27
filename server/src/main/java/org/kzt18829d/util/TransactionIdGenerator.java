package org.kzt18829d.util;

import org.kzt18829d.core.domain.BankAccount;
import org.kzt18829d.core.type.BankAccountID;
import org.kzt18829d.core.type.CurrencyType;
import static org.kzt18829d.statics.Symbols.*;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.MissingFormatArgumentException;

public class TransactionIdGenerator {
    private static final SecureRandom random = new SecureRandom();
    private static int valuteLength = 6;
    private static int bankAccountIDLength = 25;
    private static int idLength = valuteLength + 2 + bankAccountIDLength + 2 + 4 + 3 + 2 + 1 + 2 + 2 + 2 + 2+ 2+ 3 + 3+ valuteLength + bankAccountIDLength + 3;

    public static void setValuteLength(int length) {
        valuteLength = length;
    }

    public static void setBankAccountIDLength(int bankAccountIDLength) {
        TransactionIdGenerator.bankAccountIDLength = bankAccountIDLength;
    }

    // CCCCCC.LL.B*LENGTH.LL.YYYY.LLL.HH.L.MM.mm.LL.ss.DD.ms*3.LLL.FFFFFF.LLLL.G*LENGTH
    // C - валюта счёта sender
    // L - буква
    // B - номер счёта sender
    // Y - год
    // M - месяц
    // D - день
    // H - часы
    // m - минуты
    // s - секунды
    // ms- миллисекунды
    // F - валюта счёта receiver
    // G - номер счёта receiver
    public static String generate(BankAccount sender, BankAccount receiver, LocalDateTime timestamp) {
        String senderID = sender.getAccountID().getID();
        String receiverID = receiver.getAccountID().getID();
        String senderValute = CurrencyType.getCurrencyCode(sender.getCurrencyType());
        String receiverValute = CurrencyType.getCurrencyCode(receiver.getCurrencyType());

        StringBuilder transactionID = new StringBuilder(idLength);
        transactionID
                .append(senderValute)
                .append(generateLetters(2))
                .append(senderID)
                .append(generateLetters(2))
                .append(timestamp.getYear())
                .append(generateLetters(3))
                .append(timestamp.getHour())
                .append(generateLetters(1))
                .append(timestamp.getMonthValue())
                .append(timestamp.getMinute())
                .append(generateLetters(2))
                .append(timestamp.getSecond())
                .append(timestamp.getDayOfMonth())
                .append(String.valueOf(timestamp.getNano()), 0, 3)
                .append(generateLetters(3))
                .append(receiverValute)
                .append(receiverID)
                .append(generateLetters(3));

//        if (transactionID.length() != idLength)
//            throw new IllegalArgumentException("Length of transaction id more than expected idLength");

        return transactionID.toString();
    }

    private static String generateLetters(int length) {
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++)
            result.append(SYMBOLS_LET[random.nextInt(SYMBOLS_LET.length)]);

        return result.toString();
    }
}
