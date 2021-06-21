package cz.larkyy.lgenerators.generators.objects;

import cz.larkyy.lgenerators.LGenerators;
import cz.larkyy.llibrary.chat.ChatUtils;

public class Upgrade {

    private final int level;
    private final int price;
    private final int speed;
    private final String amount;
    private int minAmount;
    private int maxAmount;

    public Upgrade(int level, int price, int speed, String amount) {
        this.level = level;
        this.price = price;
        this.speed = speed;
        this.amount = amount;
        try {
            if (amount.contains("-")) {
                String[] split = amount.split("-");
                    minAmount = Integer.parseInt(split[0]);
                    maxAmount = Integer.parseInt(split[1]);
            } else {
                minAmount = Integer.parseInt(amount);
                maxAmount = Integer.parseInt(amount);
            }
        } catch (NumberFormatException e) {
            minAmount = 1;
            maxAmount = 1;
            ChatUtils.sendConsoleMsg(LGenerators.getMain(),"&c&l[!]&c Error! Number format of Amount is invalid!");
        }
    }

    public int getSpeed() {
        return speed;
    }

    public int getPrice() {
        return price;
    }

    public int getLevel() {
        return level;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public int getMinAmount() {
        return minAmount;
    }

    public String getAmount() {
        return amount;
    }
}
