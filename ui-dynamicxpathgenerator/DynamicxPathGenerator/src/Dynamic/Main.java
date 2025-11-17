package Dynamic;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/*
public class Main {
	public static void main(String[] args) throws Exception {
		Properties config = new Properties();
		String url = "";

		try (InputStream input = new FileInputStream("config.properties")) {
			config.load(input);
			url = config.getProperty("url", "").trim();

			if (url.isBlank()) {
				System.out.println("ERROR: 'url' property in config.properties is empty.");
				return;
			}

			if (!url.startsWith("http://") && !url.startsWith("https://")) {
				url = "https://" + url;
			}

			System.out.println("Launching URL: " + url);
			TestCase testCase = new TestCase();
			testCase.executeRTMUOB("AI - STREAMLINE", url);



		} catch (Exception e) {
			System.err.println("Failed to load config.properties or missing 'url' key.");
			e.printStackTrace();
		}
	}
} */


public class Main {
    public static void main(String[] args) {
        Properties config = new Properties();

        try (InputStream input = new FileInputStream("config.properties")) {
            // Load config file
            config.load(input);

            // Get application flags
            boolean isBTApp = Boolean.parseBoolean(config.getProperty("BTAPPLICATION", "false").trim());
            boolean isUOBApp = Boolean.parseBoolean(config.getProperty("UOBAPPLICATION", "false").trim());

            // Get URLs
            String btUrl = config.getProperty("BTURL", "").trim();
            String uobUrl = config.getProperty("UOBURL", "").trim();

            // Validation: both true not allowed
            if (isBTApp && isUOBApp) {
                System.out.println("ERROR: Both BTAPPLICATION and UOBAPPLICATION are set to true. No execution will happen.");
                return;
            }

            TestCase testCase = new TestCase();

            if (isBTApp) {
                if (btUrl.isBlank()) {
                    System.out.println("ERROR: 'BTURL' is empty in config.properties.");
                    return;
                }
                if (!btUrl.startsWith("http://") && !btUrl.startsWith("https://")) {
                    btUrl = "https://" + btUrl;
                }
                System.out.println("Launching BT-APPLICATION: " + btUrl);
                testCase.executeRTMUOB("AI - BTAPPLICATION", btUrl);

            } else if (isUOBApp) {
                if (uobUrl.isBlank()) {
                    System.out.println("ERROR: 'UOBURL' is empty in config.properties.");
                    return;
                }
                if (!uobUrl.startsWith("http://") && !uobUrl.startsWith("https://")) {
                    uobUrl = "https://" + uobUrl;
                }
                System.out.println("Launching UOB-APPLICATION: " + uobUrl);
                testCase.executeRTMUOB("AI - UOBAPPLICATION", uobUrl);

            } else {
                System.out.println("ERROR: Neither BTAPPLICATION nor UOBAPPLICATION is set to true. No execution will happen.");
            }

        } catch (Exception e) {
            System.err.println("Failed to load config.properties or missing required keys.");
            e.printStackTrace();
        }
    }
}

