package cz.larkyy.lgenerators.inventory;

import cz.larkyy.lgenerators.config.ConfigManager;
import cz.larkyy.lgenerators.config.ConfigValues;
import cz.larkyy.lgenerators.generators.objects.Generator;
import cz.larkyy.llibrary.chat.ChatUtils;
import cz.larkyy.llibrary.inventory.InventoryBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryHandler {

    public static void openInv(Player p, Generator generator) {
        p.closeInventory();

        InventoryBuilder inv = generator.getInv().copy();

        if (generator.getInv().getPlayer()!=null) {
            ConfigValues.sendMessage(p,ConfigValues.already_opened);
            return;
        }

        updateItems(generator,inv);

        generator.getInv().setPlayer(p);
        inv.setPlayer(p);
        p.openInventory(inv.getInventory());
    }

    private static void updateItems(Generator generator, InventoryBuilder inv) {

        Map<Character, ItemStack> newItems = new HashMap<>();
        for (Map.Entry<Character, ItemStack> pair : inv.getItems().entrySet()) {
            char character = pair.getKey();

            ItemStack is = pair.getValue();
            ItemMeta im = is.getItemMeta();

            if (im==null) {
                continue;
            }

            if (inv.getFunctions().containsKey(character)) {
                if (inv.getFunction(character).equals("upgrade")) {

                    if (generator.isMaxLvl()) {

                        im.setLore(
                                ChatUtils.format(
                                        ConfigManager.getConfig().getStringList(
                                                "generators." + generator.getName() + ".inventory.items." + character + ".maxLvlLore"
                                        )
                                )
                        );
                    }
                }
            }

            im.setDisplayName(replacePlaceholders(generator,im.getDisplayName()));
            if (im.getLore()!=null) {
                im.setLore(replacePlaceholders(generator, im.getLore()));
            }

            is.setItemMeta(im);
            newItems.put(pair.getKey(),is);
        }
        inv.setItems(newItems);
    }

    private static String replacePlaceholders(Generator generator, String str) {
        String string;
        string = str
                .replace("%level%",generator.getUpgrade()+"")
                .replace("%speed%",generator.getUpgrades().get(generator.getUpgrade()-1).getSpeed()+"")
                .replace("%amount%",generator.getUpgrades().get(generator.getUpgrade()-1).getAmount()+"")
                .replace("%type%",generator.getName());

        if (!generator.isMaxLvl()) {
            string = string
                    .replace("%next-amount%",generator.getUpgrades().get(generator.getUpgrade()).getAmount()+"")
                    .replace("%price%",generator.getUpgrades().get(generator.getUpgrade()).getPrice()+"")
                    .replace("%next-speed%",generator.getUpgrades().get(generator.getUpgrade()).getSpeed()+"")
                    .replace("%next-level%",(generator.getUpgrade()+1)+"");
        }

        return string;
    }

    private static List<String> replacePlaceholders(Generator generator, List<String> str) {
        List<String> newList = new ArrayList<>();
        for (String string : str) {
            newList.add(replacePlaceholders(generator, string));
        }
        return newList;
    }
}
