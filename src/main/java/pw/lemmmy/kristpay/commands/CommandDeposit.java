package pw.lemmmy.kristpay.commands;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import pw.lemmmy.kristpay.KristPay;
import pw.lemmmy.kristpay.economy.KristAccount;

import static org.spongepowered.api.command.args.GenericArguments.flags;
import static org.spongepowered.api.command.args.GenericArguments.none;
import static org.spongepowered.api.text.Text.of;
import static org.spongepowered.api.text.format.TextColors.GREEN;
import static org.spongepowered.api.text.format.TextColors.YELLOW;

public class CommandDeposit implements CommandExecutor {
	public static final CommandSpec SPEC = CommandSpec.builder()
		.description(Text.of("Shows your deposit address."))
		.permission("kristpay.command.deposit.base")
		.arguments(flags().flag("-mining").buildWith(none()))
		.executor(new CommandDeposit())
		.build();
	
	private static final EconomyService ECONOMY_SERVICE = KristPay.INSTANCE.getEconomyService();
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof User)) throw new CommandException(Text.of("Must be ran by a user."));
		User owner = (User) src;
		KristAccount ownerAccount = (KristAccount) ECONOMY_SERVICE.getOrCreateAccount(owner.getUniqueId())
			.orElseThrow(() -> new CommandException(Text.of("Failed to find your account.")));
		
		if (args.hasAny("mining")) {
			src.sendMessage(Text.builder()
				.append(Text.of(TextColors.GREEN, "Your "))
				.append(Text.of(TextColors.AQUA, TextStyles.ITALIC, "mining"))
				.append(Text.of(TextColors.GREEN, " deposit address: "))
				.append(CommandHelpers.formatAddress(ownerAccount.getDepositWallet().getAddress()))
				.build()
			);

			long duration = KristPay.INSTANCE.getDepositManager().miningManager.getNextRun()
				.toEpochMilli() - System.currentTimeMillis();
			src.sendMessage(Text.builder()
				.append(of(GREEN, "Your next mining deposit is in: "))
				.append(of(YELLOW, DurationFormatUtils.formatDurationWords(duration, true, true)))
				.append(of(GREEN, "."))
				.build()
			);
		} else {
			src.sendMessage(Text.builder()
				.append(Text.of(TextColors.GREEN, "Your deposit address: "))
				.append(Text.of(TextColors.YELLOW, owner.getName().toLowerCase()))
				.append(Text.of(TextColors.YELLOW, "@"))
				.append(Text.of(TextColors.YELLOW, KristPay.INSTANCE.getConfig().getMasterWallet().getPrimaryDepositName()))
				.build()
			);
		}
		
		return CommandResult.success();
	}
}
