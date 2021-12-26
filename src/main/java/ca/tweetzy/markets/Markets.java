package ca.tweetzy.markets;

import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.tweety.Common;
import ca.tweetzy.tweety.Messenger;
import ca.tweetzy.tweety.plugin.SimplePlugin;

/**
 * The current file has been created by Kiran Hart
 * Date Created: December 25 2021
 * Time Created: 11:05 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public final class Markets extends SimplePlugin {

	@Override
	protected void onPluginStart() {
		normalizePrefix();
	}

	public static Markets getInstance() {
		return (Markets) SimplePlugin.getInstance();
	}

	private void normalizePrefix() {
		Common.ADD_TELL_PREFIX = true;
		Common.ADD_LOG_PREFIX = true;
		Common.setLogPrefix(Settings.PREFIX + " ");
		Common.setTellPrefix(Settings.PREFIX);
		Messenger.setInfoPrefix(Settings.PREFIX + " ");
		Messenger.setAnnouncePrefix(Settings.PREFIX + " ");
		Messenger.setErrorPrefix(Settings.PREFIX + " ");
		Messenger.setQuestionPrefix(Settings.PREFIX + " ");
		Messenger.setSuccessPrefix(Settings.PREFIX + " ");
		Messenger.setWarnPrefix(Settings.PREFIX + " ");

	}
}
