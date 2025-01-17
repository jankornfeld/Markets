package ca.tweetzy.markets.guis;

import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.gui.GuiUtils;
import ca.tweetzy.core.input.ChatPrompt;
import ca.tweetzy.core.utils.NumberUtils;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.items.TItemBuilder;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.economy.Currency;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.utils.Common;
import ca.tweetzy.markets.utils.ConfigItemUtil;
import ca.tweetzy.markets.utils.Numbers;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 10 2021
 * Time Created: 1:34 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIBank extends Gui {

	private List<Currency> currencies;

	public GUIBank(Player player) {
		this.currencies = Markets.getInstance().getCurrencyBank().getPlayerCurrencies(player.getUniqueId());
		setTitle(TextUtils.formatText(Settings.GUI_BANK_TITLE.getString()));
		setAllowDrops(false);
		setAcceptsItems(false);
		setUseLockedCells(true);
		setAllowShiftClick(false);
		setDefaultItem(GuiUtils.getBorderItem(Settings.GUI_BANK_FILL_ITEM.getMaterial()));
		setRows(6);
		draw();
	}

	private void draw() {
		reset();
		pages = (int) Math.max(1, Math.ceil(this.currencies.size() / (double) 28L));

		// make border
		for (int i : Numbers.GUI_BORDER_6_ROWS) {
			setItem(i, GuiUtils.getBorderItem(Settings.GUI_BANK_BORDER_ITEM.getMaterial()));
			if (Settings.GUI_BANK_GLOW_BORDER.getBoolean()) highlightItem(i);
		}

		setPrevPage(5, 3, new TItemBuilder(Common.getItemStack(Settings.GUI_BACK_BTN_ITEM.getString())).setName(Settings.GUI_BACK_BTN_NAME.getString()).setLore(Settings.GUI_BACK_BTN_LORE.getStringList()).toItemStack());
		setButton(5, 4, ConfigItemUtil.build(Common.getItemStack(Settings.GUI_CLOSE_BTN_ITEM.getString()), Settings.GUI_CLOSE_BTN_NAME.getString(), Settings.GUI_CLOSE_BTN_LORE.getStringList(), 1, null), ClickType.LEFT, e -> e.gui.close());
		setNextPage(5, 5, new TItemBuilder(Common.getItemStack(Settings.GUI_NEXT_BTN_ITEM.getString())).setName(Settings.GUI_NEXT_BTN_NAME.getString()).setLore(Settings.GUI_NEXT_BTN_LORE.getStringList()).toItemStack());
		setOnPage(e -> draw());

		List<Currency> data = this.currencies.stream().skip((page - 1) * 28L).limit(28L).collect(Collectors.toList());
		int slot = 10;
		for (Currency currency : data) {
			setButton(slot++, ConfigItemUtil.build(currency.getItem().clone(), Settings.GUI_BANK_CURRENCY_NAME.getString(), Settings.GUI_BANK_CURRENCY_LORE.getStringList(), 1, new HashMap<String, Object>() {{
				put("%item_name%", Common.getItemName(currency.getItem().clone()));
				put("%currency_amount%", currency.getAmount());
			}}), ClickType.LEFT, e -> ChatPrompt.showPrompt(Markets.getInstance(), e.player, TextUtils.formatText(Markets.getInstance().getLocale().getMessage("prompt.enter_withdraw_amount").getMessage()), chat -> {
				String msg = chat.getMessage();
				if (msg != null && msg.length() != 0 && NumberUtils.isInt(msg) && Integer.parseInt(msg) > 0) {
					if (currency.getAmount() - Integer.parseInt(msg) >= 1) {

						currency.setAmount(currency.getAmount() - Integer.parseInt(msg));

						ItemStack itemStack = currency.getItem().clone();
						itemStack.setAmount(1);

						for (int i = 0; i < Integer.parseInt(msg); i++) {
							PlayerUtils.giveItem(e.player, itemStack);
						}
					} else {
						ItemStack itemStack = currency.getItem().clone();
						itemStack.setAmount(1);
						for (int i = 0; i < Integer.parseInt(msg); i++) {
							PlayerUtils.giveItem(e.player, itemStack);
						}

						Markets.getInstance().getCurrencyBank().removeCurrency(e.player.getUniqueId(), currency.getItem(), currency.getItem().getAmount());
					}

					Markets.getInstance().getLocale().getMessage("money_remove_custom_currency").processPlaceholder("price", msg).processPlaceholder("currency_item", Common.getItemName(currency.getItem())).sendPrefixedMessage(e.player);
					e.manager.showGUI(e.player, new GUIBank(e.player));
				}
			}).setOnCancel(() -> e.manager.showGUI(e.player, new GUIBank(e.player))).setOnClose(() -> e.manager.showGUI(e.player, new GUIBank(e.player))));
		}
	}
}
