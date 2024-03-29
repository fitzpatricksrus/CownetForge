package us.cownet.examplemodtests.testUtilities;

import us.cownet.examplemod.utilities.Logging;
import us.cownet.examplemod.utilities.commands.Command;
import us.cownet.examplemod.utilities.commands.GenericCommand;
import us.cownet.examplemodtests.utilities.GenericCommandTest;
import us.cownet.examplemodtests.utilities.SettingAccessorTest;
import us.cownet.examplemodtests.utilities.TypeConversionHelperTest;
import net.minecraft.command.ICommandSender;

public class RunTestsCommand extends GenericCommand {
	public static final String name = "RunTests";
	public static final String usage = "RunTests [ <testClassName> ]";
	public static final String[] aliases = {"runtests", "runTests", "RunTest", "runTest", "runtest"};

	public RunTestsCommand() {
		super(name, usage, aliases);
	}

	@Command
	public void doIt(ICommandSender sender) {
		TestExecution result0 = TestExecution.run(TypeConversionHelperTest.class, Logging.logger);
		TestExecution result1 = TestExecution.run(SettingAccessorTest.class, Logging.logger);
		TestExecution result2 = TestExecution.run(GenericCommandTest.class, Logging.logger);

		int testsFailures = result0.testFailures.size() + result1.testFailures.size() + result2.testFailures.size();
		int testsRun = result0.testOrder.size() + result1.testOrder.size() + result2.testOrder.size();

		sendMsg(sender, String.format("%d tests run.  %d passed.  %d failed.", testsRun, testsRun - testsFailures, testsFailures));
	}

	@Command
	public void doIt(ICommandSender sender, String testClassName) {
		try {
			Class<?> clazz = Class.forName(testClassName);
			if (TestExecution.Suite.class.isAssignableFrom(clazz)) {
				TestExecution results = TestExecution.run((Class<? extends TestExecution.Suite>) clazz, Logging.logger);
				int testsFailures = results.testFailures.size();
				int testsRun = results.testOrder.size();
				sendMsg(sender, String.format("%d tests fun.  %d passed.  %d failed.", testsRun, testsRun - testsFailures, testsFailures));
			}
		} catch (ClassNotFoundException | ClassCastException e) {
			sendMsg(sender, "Test class '" + testClassName + "' not found.");
		}
	}
}
