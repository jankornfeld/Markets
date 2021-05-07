package ca.tweetzy.markets.commands;

import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.compatibility.CompatibleHand;
import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.core.utils.NumberUtils;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.events.MarketItemAddEvent;
import ca.tweetzy.markets.market.Market;
import ca.tweetzy.markets.market.contents.MarketCategory;
import ca.tweetzy.markets.market.contents.MarketItem;
import ca.tweetzy.markets.utils.Common;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 04 2021
 * Time Created: 12:51 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandAddItem extends AbstractCommand {

    public CommandAddItem() {
        super(CommandType.PLAYER_ONLY, "add item");
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        if (args.length < 2) return ReturnType.SYNTAX_ERROR;
        Player player = (Player) sender;

        Market market = Markets.getInstance().getMarketManager().getMarketByPlayer(player);
        if (market == null) {
            Markets.getInstance().getLocale().getMessage("market_required").sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        if (market.getCategories().isEmpty()) {
            Markets.getInstance().getLocale().getMessage("market_category_required").sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        ItemStack heldItem = Common.getItemInHand(player).clone();
        if (heldItem.getType() == XMaterial.AIR.parseMaterial()) {
            Markets.getInstance().getLocale().getMessage("nothing_in_hand").sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        MarketCategory marketCategory = market.getCategories().stream().filter(category -> category.getName().equalsIgnoreCase(args[0])).findFirst().orElse(null);

        if (marketCategory == null) {
            Markets.getInstance().getLocale().getMessage("market_category_not_found").processPlaceholder("market_category_name", args[0]).sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        if (!NumberUtils.isDouble(args[1])) {
            Markets.getInstance().getLocale().getMessage("not_a_number").sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        boolean isPriceForStack = args.length == 3 && Arrays.asList("yes", "true", "Yes", "True").contains(args[2]);
        // if the item qty is 1, then the price will have to be for the entire stack
        if (heldItem.getAmount() == 1) isPriceForStack = true;

        MarketItem marketItem = new MarketItem(marketCategory, heldItem, Double.parseDouble(args[1]), isPriceForStack);
        MarketItemAddEvent marketItemAddEvent = new MarketItemAddEvent(market, marketItem);
        Bukkit.getPluginManager().callEvent(marketItemAddEvent);
        if (marketItemAddEvent.isCancelled()) return ReturnType.FAILURE;

        market.setUpdatedAt(System.currentTimeMillis());
        Markets.getInstance().getMarketManager().addItemToCategory(marketCategory, marketItem);
        Markets.getInstance().getLocale().getMessage("added_item_to_category").processPlaceholder("item_name", Common.getItemName(heldItem)).processPlaceholder("market_category_name", marketCategory.getName()).sendPrefixedMessage(player);
        PlayerUtils.takeActiveItem(player, CompatibleHand.MAIN_HAND, heldItem.getAmount());
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        Player player = (Player) sender;
        if (args.length == 1)
            return Markets.getInstance().getMarketManager().getMarketByPlayer(player).getCategories().stream().map(MarketCategory::getName).collect(Collectors.toList());
        if (args.length == 2) return Arrays.asList("1", "2", "3", "4", "5");
        if (args.length == 3) return Arrays.asList("true", "false");
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "markets.cmd.additem";
    }

    @Override
    public String getSyntax() {
        return "add item <category> <price> [priceIsForStack]";
    }

    @Override
    public String getDescription() {
        return "Add a new item to your market";
    }
}