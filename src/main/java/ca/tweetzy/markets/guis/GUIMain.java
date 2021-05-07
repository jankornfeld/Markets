package ca.tweetzy.markets.guis;

import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.items.TItemBuilder;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.guis.market.GUIMarketEdit;
import ca.tweetzy.markets.guis.market.GUIMarketList;
import ca.tweetzy.markets.guis.requests.GUIOpenRequests;
import ca.tweetzy.markets.market.Market;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.utils.Common;
import ca.tweetzy.markets.utils.Numbers;
import org.bukkit.entity.Player;

import java.util.Objects;

/**
 * The current file has been created by Kiran Hart
 * Date Created: April 30 2021
 * Time Created: 4:20 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIMain extends Gui {

    final Player player;

    public GUIMain(Player player) {
        this.player = player;
        setTitle(TextUtils.formatText(Settings.GUI_MAIN_TITLE.getString()));
        setAllowDrops(false);
        setAcceptsItems(false);
        setDefaultItem(Settings.GUI_MAIN_FILL_ITEM.getMaterial().parseItem());
        setUseLockedCells(Settings.GUI_MAIN_FILL_SLOTS.getBoolean());
        setRows(6);

        draw();
    }

    private void draw() {
        if (Settings.GUI_MAIN_USE_BORDER.getBoolean()) {
            for (int i : Numbers.GUI_BORDER_6_ROWS) {
                setItem(i, Settings.GUI_MAIN_BORDER_ITEM.getMaterial().parseItem());
            }

            if (Settings.GUI_MAIN_GLOW_BORDER.getBoolean()) {
                for (int i : Numbers.GUI_BORDER_6_ROWS) highlightItem(i);
            }
        }

        setButton(2, 2, new TItemBuilder(Settings.GUI_MAIN_ITEMS_ALL_MARKETS_ITEM_USE_CUSTOM_HEAD.getBoolean() ? Common.getCustomTextureHead(String.format("%s%s", "http://textures.minecraft.net/texture/", Settings.GUI_MAIN_ITEMS_ALL_MARKETS_ITEM_CUSTOM_HEAD_LINK.getString()), false) : Settings.GUI_MAIN_ITEMS_ALL_MARKETS_ITEM.getMaterial().parseItem()).setName(Settings.GUI_MAIN_ITEMS_ALL_MARKETS_NAME.getString()).setLore(Settings.GUI_MAIN_ITEMS_ALL_MARKETS_LORE.getStringList()).toItemStack(), e -> {
            e.manager.showGUI(this.player, new GUIMarketList());
        });

        setButton(2, 4, new TItemBuilder(Common.getPlayerHead(this.player)).setName(Settings.GUI_MAIN_ITEMS_YOUR_MARKET_NAME.getString()).setLore(Settings.GUI_MAIN_ITEMS_YOUR_MARKET_LORE.getStringList()).toItemStack(), e -> {
            Market market = Markets.getInstance().getMarketManager().getMarketByPlayer(player);
            if (market != null) {
                e.manager.showGUI(this.player, new GUIMarketEdit(market));
            }
        });

        setButton(2, 6, new TItemBuilder(Objects.requireNonNull(Settings.GUI_MAIN_ITEMS_REQUESTS_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_MAIN_ITEMS_REQUESTS_NAME.getString()).setLore(Settings.GUI_MAIN_ITEMS_REQUESTS_LORE.getStringList()).toItemStack(), e -> {
            e.manager.showGUI(this.player, new GUIOpenRequests(this.player));
        });

        setButton(3, 3, new TItemBuilder(Objects.requireNonNull(Settings.GUI_MAIN_ITEMS_CHEST_SHOPS_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_MAIN_ITEMS_CHEST_SHOPS_NAME.getString()).setLore(Settings.GUI_MAIN_ITEMS_CHEST_SHOPS_LORE.getStringList()).toItemStack(), e -> {

        });

        setButton(3, 5, new TItemBuilder(Objects.requireNonNull(Settings.GUI_MAIN_ITEMS_TRANSACTIONS_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_MAIN_ITEMS_TRANSACTIONS_NAME.getString()).setLore(Settings.GUI_MAIN_ITEMS_TRANSACTIONS_LORE.getStringList()).toItemStack(), e -> {
            e.manager.showGUI(this.player, new GUITransactionView(this.player));
        });
    }
}