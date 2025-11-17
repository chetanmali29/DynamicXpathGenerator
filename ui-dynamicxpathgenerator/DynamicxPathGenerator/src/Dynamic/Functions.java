package Dynamic;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.function.Consumer;
public class Functions {
	Operations m_Operation = null;
	private WebDriver driver;
	//private static final String JSON_FILE_PATH = "C:\\ui-btdmvautomation\\TestxPath\\OR_Main.json";
	private static final String JSON_FILE_PATH = System.getProperty("user.dir")+"\\OR_Main.json";
	public Functions() {
		m_Operation=new Operations();
		//    	this.driver = driver;
	}


	public String invokeBrowser(String Url)
	{
		String intRet="";
		try 
		{
			System.setProperty("webdriver.chrome.silentOutput", "true");
			//String profilename="";
			//intRet = m_Operation.invokeBrowser();
			intRet = m_Operation.invokeBrowserUsingUrl(Url);
			if(intRet.equals("0"))
			{
				return "0";
			}
			else
			{
				return intRet;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return "ERR001"; // Exception while invoking browser
		}
	}

	public String login() {
		String intRet = "0";
		WebDriver driver = Operations.getDriver();
		try {
			//System.out.println("--------------Extracting elements on Login Page--------------");
			Operations.extractInputsWithLabels(driver);
			Operations.extractButtons(driver);
			Operations.extractCheckboxesAndRadioButtons(driver);
			Thread.sleep(3000);
			//System.out.println("Populating login credentials...");
			JSONObject jsonObject = new JSONObject(new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH))));

			// Categorize fields
			List<String> textFields = new ArrayList<>();
			List<String> checkboxesRadios = new ArrayList<>();
			List<String> buttons = new ArrayList<>();

			//ADD json object inside the array
			for (String key : jsonObject.keySet()) {
				JSONObject fieldData = jsonObject.getJSONObject(key);
				String type = fieldData.optString("type", "").toLowerCase();

				if (type.equals("text") || type.equals("email") || type.equals("password")) {
					textFields.add(key);
				} else if (type.equals("checkbox") || type.equals("radio")) {
					checkboxesRadios.add(key);
				} else if (type.equals("submit") || type.equals("button")) {
					buttons.add(key);
				}
			}

			// 1. Fill text fields
			for (String key : textFields) {
				JSONObject fieldData = jsonObject.getJSONObject(key);
				String xpath = fieldData.optString("xpath");
				WebElement element = driver.findElement(By.xpath(xpath));
				element.clear();
				Thread.sleep(1000);
				String lowerKey = key.toLowerCase();
				if (lowerKey.contains("company id")|| (lowerKey.contains("realm"))) {
					element.sendKeys("GOLIVEFASTER");
				} else if (lowerKey.contains("user id") || (lowerKey.contains("username"))) {
					element.sendKeys("CHETANMALI");
				} else if (lowerKey.contains("password")) {
					element.sendKeys("Password@8");
				} else {
					element.sendKeys("testValue"); // default
				}
			}
			
			// 2. Click checkboxes or radio buttons
			for (String key : checkboxesRadios) {
				JSONObject fieldData = jsonObject.getJSONObject(key);
				String xpath = fieldData.optString("xpath");
				WebElement element = driver.findElement(By.xpath(xpath));
				if (!element.isSelected()) element.click();
			}
			
			// 3. Click login/submit buttons
			for (String key : buttons) {
				Operations.extractButtons(driver);
				Thread.sleep(1000);
				JSONObject jsonObject1 = new JSONObject(new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH))));
				JSONObject fieldData = jsonObject1.getJSONObject(key);
				Thread.sleep(1000);

				if (key.toLowerCase().contains("login") || key.toLowerCase().contains("sign in"))  {
					String xpath = fieldData.optString("xpath");
					WebElement element = driver.findElement(By.xpath(xpath));
					element.click();
					Thread.sleep(4000);
					break;
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
			intRet = "1";
		}
		return intRet;
	}

	
/*
	public String login2() {
	    String intRet = "0";
	    WebDriver driver = Operations.getDriver();

	    // Suppress System.out globally
	    PrintStream originalOut = System.out;
	    System.setOut(new PrintStream(OutputStream.nullOutputStream())); // Java 11+

	    try {
	        // Suppress all internal method prints
	        Operations.extractInputsWithLabels(driver);
	        Operations.extractButtons(driver);
	        Operations.extractCheckboxesAndRadioButtons(driver);
	        Thread.sleep(3000);

	        JSONObject jsonObject = new JSONObject(new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH))));

	        List<String> textFields = new ArrayList<>();
	        List<String> checkboxesRadios = new ArrayList<>();
	        List<String> buttons = new ArrayList<>();

	        for (String key : jsonObject.keySet()) {
	            JSONObject fieldData = jsonObject.getJSONObject(key);
	            String type = fieldData.optString("type", "").toLowerCase();

	            if (type.equals("text") || type.equals("email") || type.equals("password")) {
	                textFields.add(key);
	            } else if (type.equals("checkbox") || type.equals("radio")) {
	                checkboxesRadios.add(key);
	            } else if (type.equals("submit") || type.equals("button")) {
	                buttons.add(key);
	            }
	        }

	        // Restore output just before important visible output
	        System.setOut(originalOut);
	        System.out.println("Authenticating your credentials...");

	        for (String key : textFields) {
	            JSONObject fieldData = jsonObject.getJSONObject(key);
	            String xpath = fieldData.optString("xpath");
	            WebElement element = driver.findElement(By.xpath(xpath));
	            element.clear();
	            Thread.sleep(1000);

	            String lowerKey = key.toLowerCase();
	            if (lowerKey.contains("company id") || lowerKey.contains("realm")) {
	                element.sendKeys("GOLIVEFASTER");
	            } else if (lowerKey.contains("user id") || lowerKey.contains("username")) {
	                element.sendKeys("CHETANMALI");
	            } else if (lowerKey.contains("password")) {
	                element.sendKeys("Password@8");
	            } else {
	                element.sendKeys("testValue");
	            }
	        }

	        for (String key : checkboxesRadios) {
	            JSONObject fieldData = jsonObject.getJSONObject(key);
	            String xpath = fieldData.optString("xpath");
	            WebElement element = driver.findElement(By.xpath(xpath));
	            if (!element.isSelected()) element.click();
	        }

	        for (String key : buttons) {
	            // Suppress noisy output again for extractButtons
	            System.setOut(new PrintStream(OutputStream.nullOutputStream()));
	            Operations.extractButtons(driver);
	            System.setOut(originalOut); // Restore before real action

	            Thread.sleep(1000);
	            JSONObject jsonObject1 = new JSONObject(new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH))));
	            JSONObject fieldData = jsonObject1.getJSONObject(key);
	            Thread.sleep(1000);

	            if (key.toLowerCase().contains("login") || key.toLowerCase().contains("sign in")) {
	                String xpath = fieldData.optString("xpath");
	                WebElement element = driver.findElement(By.xpath(xpath));
	                element.click();
	                Thread.sleep(4000);
	                break;
	            }
	        }

	    } catch (Exception e) {
	        System.setOut(originalOut); // Ensure reset even on error
	        intRet = "1";
	    }

	    System.setOut(originalOut); // Final reset just in case
	    return intRet;
	}
*/
	public String login2() {
	    String intRet = "0";
	    WebDriver driver = Operations.getDriver();
	    PrintStream originalOut = System.out;

	    // Load credentials from config.properties
	    Properties config = new Properties();
	    String companyId = "";
	    String username = "";
	    String password = "";

	    try {
	        config.load(new FileInputStream("config.properties"));
	        companyId = config.getProperty("companyid", "").trim();
	        username = config.getProperty("username", "").trim();
	        password = config.getProperty("password", "").trim();
	    } catch (Exception e) {
	        System.setOut(originalOut);
	        System.err.println("Error loading credentials from config.properties.");
	        e.printStackTrace();
	        return "1";
	    }

	    // Suppress all internal prints
	    System.setOut(new PrintStream(OutputStream.nullOutputStream()));

	    try {
	        Operations.extractInputsWithLabels(driver);
	        Operations.extractButtons(driver); // ---> MADE CHANGES 09/12/2025
	        Operations.extractCheckboxesAndRadioButtons(driver);
	        Thread.sleep(3000);

	        JSONObject jsonObject = new JSONObject(new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH))));

	        List<String> textFields = new ArrayList<>();
	        List<String> checkboxesRadios = new ArrayList<>();
	        List<String> buttons = new ArrayList<>();

	        for (String key : jsonObject.keySet()) {
	            JSONObject fieldData = jsonObject.getJSONObject(key);
	            String type = fieldData.optString("type", "").toLowerCase();
	            String textFormat = fieldData.optString("text", "").toLowerCase();
	            if (type.equals("text") || type.equals("email") || type.equals("password")) {
	                textFields.add(key);
	            } else if (type.equals("checkbox") || type.equals("radio")) {
	                checkboxesRadios.add(key);
	            } else if (type.equals("submit") || type.equals("button")) {
	                buttons.add(key);
	            }    else if (fieldData.optString("tag", "").equalsIgnoreCase("button")) {
	                    buttons.add(key);
	                }
	        }

	        // Restore output before main interaction
	        System.setOut(originalOut);
	        System.out.println("Authenticating your credentials...");

	        for (String key : textFields) {
	            JSONObject fieldData = jsonObject.getJSONObject(key);
	            String xpath = fieldData.optString("xpath");
	            WebElement element = driver.findElement(By.xpath(xpath));
	            element.clear();
	            Thread.sleep(1000);

	            String lowerKey = key.toLowerCase();
	            if (lowerKey.contains("company id") || lowerKey.contains("realm")) {
	                element.sendKeys(companyId);
	            } else if (lowerKey.contains("user id") || lowerKey.contains("username")) {
	                element.sendKeys(username);
	            } else if (lowerKey.contains("password")) {
	                element.sendKeys(password);
	            } else {
	                element.sendKeys("testValue");
	            }
	        }

	        for (String key : checkboxesRadios) {
	            JSONObject fieldData = jsonObject.getJSONObject(key);
	            String xpath = fieldData.optString("xpath");
	            WebElement element = driver.findElement(By.xpath(xpath));
	            if (!element.isSelected()) element.click();
	        }

	        for (String key : buttons) {
	            // Suppress again during re-extraction
	            System.setOut(new PrintStream(OutputStream.nullOutputStream()));
	            Operations.extractButtons(driver);
	            System.setOut(originalOut);

	            Thread.sleep(1000);
	            JSONObject jsonObject1 = new JSONObject(new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH))));
	            JSONObject fieldData = jsonObject1.getJSONObject(key);
	            Thread.sleep(1000);

	            if (key.toLowerCase().contains("login") || key.toLowerCase().contains("sign in")) {
	                String xpath = fieldData.optString("xpath");
	                WebElement element = driver.findElement(By.xpath(xpath));
	                element.click();
	                Thread.sleep(4000);
	                break;
	            }
	        }

	    } catch (Exception e) {
	    	
	        System.setOut(originalOut);
	        intRet = "1";
	    }

	    System.setOut(originalOut);
	    return intRet;
	}
	
	public String clickOnDefectManagement() {


		String intRet = "0";
		WebDriver driver = Operations.getDriver();
		try {
			System.out.println("--------------Extracting elements on DashBoard--------------");
			Thread.sleep(3000);
			Operations.extractNavigationElements(driver);
			Thread.sleep(5000);
			System.out.println("Populated DashBoard...");
			JSONObject jsonObject = new JSONObject(new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH))));

			// Categorize fields for NAV BAR
			List<String> navBar = new ArrayList<>();

			for (String key : jsonObject.keySet()) {
				JSONObject fieldData = jsonObject.getJSONObject(key);
				//String type = fieldData.optString("type", "").toLowerCase();
				String tag = fieldData.optString("tag","").toLowerCase();
				if (tag.equals("li")) {
					navBar.add(key);
				} 
			}
			// 3. Click to defect management buttons
			for (String key : navBar) {
				JSONObject fieldData = jsonObject.getJSONObject(key);
				if (key.toLowerCase().contains("defect"))  {
					String xpath = fieldData.optString("xpath");
					WebElement element = driver.findElement(By.xpath(xpath));
					element.click();
					Thread.sleep(4000);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			intRet = "1";
		}
		return intRet;

	}


	public String clickOnAddDefect() {
		String intRet = "0";
		WebDriver driver = Operations.getDriver();
		try {
			System.out.println("--------------Extracting elements for Add a defect--------------");
			Operations.extractButtons(driver);
			Thread.sleep(3500);
			System.out.println("Populated Button for Add a defect....");
			JSONObject jsonObject = new JSONObject(new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH))));
			List<String> buttons = new ArrayList<>();
			for (String key : jsonObject.keySet()) {
				JSONObject fieldData = jsonObject.getJSONObject(key);
				String type = fieldData.optString("type", "").toLowerCase();

				if (type.equals("button")) {
					buttons.add(key);
				}
			}
			for (String key : buttons) {
				Thread.sleep(1000);
				JSONObject fieldData = jsonObject.getJSONObject(key);
				Thread.sleep(1000);
				if (key.toLowerCase().contains("add defect"))  {
					String xpath = fieldData.optString("xpath");
					String xpaths = xpath.replace("ADD DEFECT", "Add Defect");
					WebElement element = driver.findElement(By.xpath(xpaths));
					element.click();
					Thread.sleep(1500);
					break;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			intRet = "1";
		}
		return intRet;
	}


	public String FillDefectInformation() {
		String intRet = "0";

		WebDriver driver = Operations.getDriver();
		try {
			Thread.sleep(2000);
			System.out.println("--------------Extracting elements for Adding Info of defect--------------");
			Operations.extractInputsWithLabels(driver);
			Operations.extractButtons(driver);
			Operations.extractCheckboxesAndRadioButtons(driver);
			Operations.extractDropdownsWithLabels(driver);

			Thread.sleep(4000);
			System.out.println("Populated Adding Info of Defect Xpath...");
			System.out.println("------Extracted xPath as Follows:------ \n 1.Login Page \n 2.Defect Managment Tab \n 3.Fill Defect Information Page");
		} catch (Exception e) {
			e.printStackTrace();
			intRet = "1";
		}
		return intRet;
	}


	public String smartClickHandler()
	{
		String intRet = "0";
		WebDriver driver = Operations.getDriver();
		try {String Value2 =
				//startXPathCaptureModeNativeModified2(driver);
				
		startXPathCaptureModeNativeFuntionalities(driver);
		if(Value2.contains("ERR003"))
		{
			return "ERR003";
		}
		} catch (Exception e) {
			return "ERR001";
		}
		return "0";
	}

	
	public static String startXPathCaptureModeNativeFuntionalities1(WebDriver driver) {
		String currentWindowHandle = driver.getWindowHandle();
	    JavascriptExecutor js = (JavascriptExecutor) driver;
	    AtomicBoolean fullCaptureRunning = new AtomicBoolean(false);
	    AtomicBoolean tabCaptured = new AtomicBoolean(false);
	    AtomicReference<String> captureState = new AtomicReference<>("idle"); // idle | start | pause
	    
	    // Inject click tracker
	    js.executeScript(
	        "document.addEventListener('click', function(e) {" +
	        "    e.target.setAttribute('data-capture-click', 'true');" +
	        "}, true);"
	    );

	    // Inject TAB key tracker
	    js.executeScript(
	        "document.addEventListener('keydown', function(e) {" +
	        "    if (e.key === 'Tab') {" +
	        "        document.body.setAttribute('data-tab-pressed', 'true');" +
	        "    }" +
	        "}, true);"
	    );

	    // Inject JS-based XPath generator
	    js.executeScript(
	        "window.generateXPath = function(el) {" +
	        "  if (el.id) return '//*[@id=\"' + el.id + '\"]';" +
	        "  if (el === document.body) return '/html/body';" +
	        "  var ix = 0;" +
	        "  var siblings = el.parentNode ? el.parentNode.childNodes : [];" +
	        "  for (var i = 0; i < siblings.length; i++) {" +
	        "    var sibling = siblings[i];" +
	        "    if (sibling === el) return window.generateXPath(el.parentNode) + '/' + el.tagName.toLowerCase() + '[' + (ix + 1) + ']';" +
	        "    if (sibling.nodeType === 1 && sibling.tagName === el.tagName) ix++;" +
	        "  }" +
	        "};"
	    );
	    System.out.println("Please type 'start' to begin capturing xpaths dynamically.");
	    try (Scanner scanner = new Scanner(System.in)) {
	        while (true) {
	        	/* Changes for the new TAB EVENT - 09/12/2025 */
	        	
	        	Set<String> knownHandles = new HashSet<>(driver.getWindowHandles());
	        	 
	        	 Set<String> allHandles = driver.getWindowHandles();

	             // Detect closed tab
	             if (!allHandles.contains(currentWindowHandle)) {
	                 if (!allHandles.isEmpty()) {
	                     currentWindowHandle = allHandles.iterator().next(); // fallback
	                     driver.switchTo().window(currentWindowHandle);
	                     System.out.println("Current tab was closed. Switched to another open tab.");
	                 } else {
	                     System.out.println("All tabs are closed. Exiting capture mode.");
	                     return "STP002";
	                 }
	             }

	             // Detect newly opened tab
	             if (allHandles.size() > knownHandles.size()) {
	                 // Find the new handle
	                 for (String handle : allHandles) {
	                     if (!knownHandles.contains(handle)) {
	                         currentWindowHandle = handle;
	                         driver.switchTo().window(handle);
	                         System.out.println("New tab detected. Switched to new tab for XPath capture.");
	                         break;
	                     }
	                 }
	             }

	             // Update known handles for next loop
	             knownHandles = new HashSet<>(allHandles);
	        	
	            // If capture is "start", check click/TAB events
	            if ("start".equals(captureState.get())) {
	                try {
	                    Object tabPressed = js.executeScript("return document.body.getAttribute('data-tab-pressed');");
	                    if ("true".equals(tabPressed)) {
	                        tabCaptured.set(true);
	                        js.executeScript("document.body.removeAttribute('data-tab-pressed');");
	                    }
	                } catch (Exception e) {
	                    System.err.println("Error checking tab press: " + e.getMessage());
	                }

	                List<WebElement> clicked = driver.findElements(By.cssSelector("[data-capture-click='true']"));
	                Thread.sleep(400);

	                if (!clicked.isEmpty()) {
	                    boolean anyCaptured = false;
	                    for (WebElement el : clicked) {
	                        try {
	                            String value = Operations.clickAndCaptureNewNative2(el, driver);
	                            js.executeScript("arguments[0].removeAttribute('data-capture-click');", el);
	                            Thread.sleep(400);
	                            if (!value.equals("-1")) {
	                                anyCaptured = true;
	                            } else {
	                                String jsXPath = (String) js.executeScript("return window.generateXPath(arguments[0]);", el);
	                                System.out.println("JS fallback XPath: " + jsXPath);
	                            }
	                        } catch (StaleElementReferenceException staleEx) {
	                            System.out.println("Stale element clicked. Please try again.");
	                        }
	                    }

	                    if (!anyCaptured) {
	                        System.out.println("The element is not captured through Click Event");
	                    }
	                    Thread.sleep(700);
	                }

	                if (tabCaptured.get() && !fullCaptureRunning.get()) {
	                    tabCaptured.set(false);
	                    try {
	                        WebElement focusedElement = driver.switchTo().activeElement();
	                        if (focusedElement != null) {
	                            String xpath = Operations.getXPathOfElementUsingTAB(driver, focusedElement);
	                            Operations.saveElementToJsonUsingTAB(focusedElement, xpath);
	                            System.out.println("Captured XPath for element focused by TAB: " + xpath);
	                        } else {
	                            System.out.println("No element is currently focused.");
	                        }
	                    } catch (StaleElementReferenceException e) {
	                        System.err.println("Focused element is stale. Skipping TAB capture.");
	                    } catch (Exception e) {
	                        System.err.println("Error during TAB-based capture: " + e.getMessage());
	                    }
	                }
	            }

	            // Check CLI input
	            if (System.in.available() > 0) {
	                String command = scanner.nextLine().trim().toLowerCase();
	                 switch (command) {
	                    case "start":
	                    	
	                    	Operations.saveOrUpdateDateTimestamp3(true);  // 
	                    	Operations.sortJsonByClickCounterPreservingOthers();
	                        captureState.set("start");
	                        System.out.println("-------------------------------");
	                        System.out.println("== XPath capture mode STARTED ==");
	                        System.out.println("-> Click on elements in the browser to capture their XPath.");
	                        System.out.println("-> Type 'capture full' in the console to extract all elements.");
	                        System.out.println("-> Click on TAB Button to capture mode.");
	                        System.out.println("-> Type Object name in the console to extract the particular element.");
							System.out.println("-> Type 'Screenshot' to capture the screenshot.");
	                        System.out.println("-> Type 'pause' to pause capturing, 'continue' to resume, or 'stop' to exit.");
	                        System.out.println("-------------------------------");
	                        
	                        break;
	                    case "pause":
	                        if (!"start".equals(captureState.get())) {
	                            System.out.println("Please start the capture first using 'start'");
	                        } else {
	                            captureState.set("pause");
	                            System.out.println("The XPath capturing mode is paused until 'continue' is passed.");

	                            // 🧹 Clear any previously recorded clicks or TABs to avoid stale captures
	                            js.executeScript("document.querySelectorAll('[data-capture-click]').forEach(el => el.removeAttribute('data-capture-click'));");
	                            js.executeScript("document.body.removeAttribute('data-tab-pressed');");
	                        }
	                        break;

	                    case "continue":
	                        String state = captureState.get();
	                        if (!"pause".equals(state) && !"start".equals(state)) {
	                            System.out.println("Please start the capture first using 'start'");
	                        } else {
	                            captureState.set("start");
	                            System.out.println("The XPath capturing mode is continued. Please proceed.");

	                            // 🧹 Clear any lingering clicks or TABs that happened during pause
	                            js.executeScript("document.querySelectorAll('[data-capture-click]').forEach(el => el.removeAttribute('data-capture-click'));");
	                            js.executeScript("document.body.removeAttribute('data-tab-pressed');");
	                        }
	                        break;


	                    case "stop":
	                        System.out.println("== XPath capture mode STOPPED ==");
	                        Operations.quitDriver(driver);
	                        return "STP001";

	                    case "dropdown":
	                        if (!"start".equals(captureState.get())) {
	                            System.out.println(" Please start the capture first using 'start'");
	                        } else {
	                            Operations.getAllDynamicDropdownContainers_DynamicXpathGenerator();
	                            Operations.getAllPopUpDropdownContainers_DynamicXpathGenerator();
	                        }
	                        break;

	                    case "capture full":
	                        if (!"start".equals(captureState.get())) {
	                            System.out.println("Please start the capture first using 'start'");
	                        } else if (!fullCaptureRunning.get()) {
	                            System.out.println("Capturing all visible XPaths from page...");
	                            fullCaptureRunning.set(true);
	                            new Thread(() -> {
	                                try {
	                                    Operations.extractInputsWithLabels(driver);
	                                    Thread.sleep(300);
	                                    Operations.extractButtons(driver);
	                                    Thread.sleep(300);
	                                    Operations.extractCheckboxesAndRadioButtons(driver);
	                                    Thread.sleep(300);
	                                    Operations.extractDropdownsWithLabels(driver);
	                                    Thread.sleep(300);
	                                    Operations.extractNavigationElements(driver);
	                                    System.out.println("== Full page XPath capture COMPLETE ==");
	                                    System.out.println("== Please proceed with Click functionality or TAB Based Functionality or Capture Full ==");
	                                } catch (Exception e) {
	                                    System.err.println("Exception Raised during full capture: " + e);
	                                } finally {
	                                    fullCaptureRunning.set(false);
	                                }
	                            }).start();
	                        } else {
	                            System.out.println("Full capture is already in progress. Please wait...");
	                        }
	                        break;
	                    case "screenshot":
	                    	//System.out.println("------------- Screenshot Mode Started-------------");
	                    	Operations.captureFullPageScreenshots2(driver);
	                    	System.out.println("------------- Screenshot Mode Exited-------------");
	                    	break;
	                    default:
	                        if (!"start".equals(captureState.get())) {
	                            System.out.println(" Please start the capture first using 'start'");
	                        } else if (!command.isEmpty()) {
	                            Operations.captureXPathByFieldName(driver, command);
	                        } else {
	                            System.out.println(" Invalid command. Allowed: start, pause, continue, stop, dropdown, capture full or object name");
	                        }
	                        break;
	                }
	            }

	            Thread.sleep(1500);
	        }
	    } catch (StaleElementReferenceException staleEx) {
	        return "ERR003";
	    } catch (Exception e) {
	        return "ERR004";
	    }
	}

	//----------------------------------------------------------------------------------------//
	
	public static String startXPathCaptureModeNativeFuntionalities(WebDriver driver) {
	    String currentWindowHandle = driver.getWindowHandle();
	    JavascriptExecutor js = (JavascriptExecutor) driver;
	    AtomicBoolean fullCaptureRunning = new AtomicBoolean(false);
	    AtomicBoolean tabCaptured = new AtomicBoolean(false);
	    AtomicReference<String> captureState = new AtomicReference<>("idle");
	    
	    // Method to inject all required JavaScript into current tab - MOVE THIS OUTSIDE THE METHOD
	    // This should be a separate method in your class, not inside startXPathCaptureModeNativeFuntionalities
	    
	    // For now, we'll inline the functionality:
	    
	    // Helper method to inject JavaScript - create inline method since js variable changes
	    Consumer<JavascriptExecutor> injectJavaScriptListeners = (jsExecutor) -> {
	        try {
	            // Inject click tracker
	            jsExecutor.executeScript(
	                "if (!window.clickListenerInjected) {" +
	                "  document.addEventListener('click', function(e) {" +
	                "    e.target.setAttribute('data-capture-click', 'true');" +
	                "  }, true);" +
	                "  window.clickListenerInjected = true;" +
	                "}"
	            );

	            // Inject TAB key tracker
	            jsExecutor.executeScript(
	                "if (!window.tabListenerInjected) {" +
	                "  document.addEventListener('keydown', function(e) {" +
	                "    if (e.key === 'Tab') {" +
	                "      document.body.setAttribute('data-tab-pressed', 'true');" +
	                "    }" +
	                "  }, true);" +
	                "  window.tabListenerInjected = true;" +
	                "}"
	            );

	            // Inject XPath generator
	            jsExecutor.executeScript(
	                "if (!window.generateXPath) {" +
	                "  window.generateXPath = function(el) {" +
	                "    if (el.id) return '//*[@id=\"' + el.id + '\"]';" +
	                "    if (el === document.body) return '/html/body';" +
	                "    var ix = 0;" +
	                "    var siblings = el.parentNode ? el.parentNode.childNodes : [];" +
	                "    for (var i = 0; i < siblings.length; i++) {" +
	                "      var sibling = siblings[i];" +
	                "      if (sibling === el) return window.generateXPath(el.parentNode) + '/' + el.tagName.toLowerCase() + '[' + (ix + 1) + ']';" +
	                "      if (sibling.nodeType === 1 && sibling.tagName === el.tagName) ix++;" +
	                "    }" +
	                "  };" +
	                "}"
	            );
	        } catch (Exception e) {
	            System.err.println("Error injecting JavaScript listeners: " + e.getMessage());
	        }
	    };
	    
	    // Initial injection for current tab
	    injectJavaScriptListeners.accept(js);
	    
	    System.out.println("Please type 'start' to begin capturing xpaths dynamically.");
	    try (Scanner scanner = new Scanner(System.in)) {
	        Set<String> knownHandles = new HashSet<>(driver.getWindowHandles());
	        
	        while (true) {
	            Set<String> allHandles = driver.getWindowHandles();

	            // Detect closed tab
	            if (!allHandles.contains(currentWindowHandle)) {
	                if (!allHandles.isEmpty()) {
	                    currentWindowHandle = allHandles.iterator().next();
	                    driver.switchTo().window(currentWindowHandle);
	                    js = (JavascriptExecutor) driver; // Update JS executor reference
	                    injectJavaScriptListeners.accept(js); // RE-INJECT for switched tab
	                    System.out.println("Current tab was closed. Switched to another open tab and re-injected listeners.");
	                } else {
	                    System.out.println("All tabs are closed. Exiting capture mode.");
	                    return "STP002";
	                }
	            }

	            // Detect newly opened tab
	            if (allHandles.size() > knownHandles.size()) {
	                for (String handle : allHandles) {
	                    if (!knownHandles.contains(handle)) {
	                        currentWindowHandle = handle;
	                        driver.switchTo().window(handle);
	                        js = (JavascriptExecutor) driver; // Update JS executor reference
	                        
	                        // Wait a moment for new tab to fully load
	                        try {
	                            Thread.sleep(1000);
	                        } catch (InterruptedException e) {
	                            Thread.currentThread().interrupt();
	                        }
	                        
	                        injectJavaScriptListeners.accept(js); // INJECT listeners for new tab
	                        System.out.println("New tab detected. Switched to new tab and injected XPath capture listeners.");
	                        break;
	                    }
	                }
	            }

	            // Update known handles for next loop
	            knownHandles = new HashSet<>(allHandles);

	            // If capture is "start", check click/TAB events
	            if ("start".equals(captureState.get())) {
	                try {
	                    // Check if current page is ready and has our listeners
	                    Object listenerCheck = js.executeScript("return window.clickListenerInjected && window.tabListenerInjected;");
	                    if (!Boolean.TRUE.equals(listenerCheck)) {
	                        System.out.println("Re-injecting listeners for current tab...");
	                        injectJavaScriptListeners.accept(js);
	                    }
	                    
	                    Object tabPressed = js.executeScript("return document.body.getAttribute('data-tab-pressed');");
	                    if ("true".equals(tabPressed)) {
	                        tabCaptured.set(true);
	                        js.executeScript("document.body.removeAttribute('data-tab-pressed');");
	                    }
	                } catch (Exception e) {
	                    System.err.println("Error checking tab press: " + e.getMessage());
	                    // Try to re-inject listeners if there's an error
	                    try {
	                        injectJavaScriptListeners.accept(js);
	                    } catch (Exception retryEx) {
	                        System.err.println("Failed to re-inject listeners: " + retryEx.getMessage());
	                    }
	                }

	                List<WebElement> clicked = null;
	                try {
	                    clicked = driver.findElements(By.cssSelector("[data-capture-click='true']"));
	                } catch (Exception e) {
	                    System.err.println("Error finding clicked elements: " + e.getMessage());
	                    clicked = new ArrayList<>();
	                }
	                
	                Thread.sleep(400);

	                if (!clicked.isEmpty()) {
	                    boolean anyCaptured = false;
	                    for (WebElement el : clicked) {
	                        try {
	                            String value = Operations.clickAndCaptureNewNative2(el, driver);
	                            js.executeScript("arguments[0].removeAttribute('data-capture-click');", el);
	                            Thread.sleep(400);
	                            if (!value.equals("-1")) {
	                                anyCaptured = true;
	                            } else {
	                                String jsXPath = (String) js.executeScript("return window.generateXPath(arguments[0]);", el);
	                                System.out.println("JS fallback XPath: " + jsXPath);
	                            }
	                        } catch (StaleElementReferenceException staleEx) {
	                            System.out.println("Stale element clicked. Please try again.");
	                        } catch (Exception e) {
	                            System.err.println("Error processing clicked element: " + e.getMessage());
	                        }
	                    }

	                    if (!anyCaptured) {
	                        System.out.println("The element is not captured through Click Event");
	                    }
	                    Thread.sleep(700);
	                }

	                if (tabCaptured.get() && !fullCaptureRunning.get()) {
	                    tabCaptured.set(false);
	                    try {
	                        WebElement focusedElement = driver.switchTo().activeElement();
	                        if (focusedElement != null) {
	                            String xpath = Operations.getXPathOfElementUsingTAB(driver, focusedElement);
	                            Operations.saveElementToJsonUsingTAB(focusedElement, xpath);
	                            System.out.println("Captured XPath for element focused by TAB: " + xpath);
	                        } else {
	                            System.out.println("No element is currently focused.");
	                        }
	                    } catch (StaleElementReferenceException e) {
	                        System.err.println("Focused element is stale. Skipping TAB capture.");
	                    } catch (Exception e) {
	                        System.err.println("Error during TAB-based capture: " + e.getMessage());
	                    }
	                }
	            }

	            // Check CLI input
	            if (System.in.available() > 0) {
	                String command = scanner.nextLine().trim().toLowerCase();
	                switch (command) {
	                    case "start":
	                        // Re-inject listeners when starting capture to ensure they work
	                        injectJavaScriptListeners.accept(js);
	                        
	                        Operations.saveOrUpdateDateTimestamp3(true);
	                        Operations.sortJsonByClickCounterPreservingOthers();
	                        captureState.set("start");
	                        System.out.println("-------------------------------");
	                        System.out.println("== XPath capture mode STARTED ==");
	                        System.out.println("-> Click on elements in the browser to capture their XPath.");
	                        System.out.println("-> Type 'capture full' in the console to extract all elements.");
	                        System.out.println("-> Click on TAB Button to capture mode.");
	                        System.out.println("-> Type Object name in the console to extract the particular element.");
	                        System.out.println("-> Type 'Screenshot' to capture the screenshot.");
	                        System.out.println("-> Type 'pause' to pause capturing, 'continue' to resume, or 'stop' to exit.");
	                        System.out.println("-------------------------------");
	                        break;
	                        
	                    case "pause":
	                        if (!"start".equals(captureState.get())) {
	                            System.out.println("Please start the capture first using 'start'");
	                        } else {
	                            captureState.set("pause");
	                            System.out.println("The XPath capturing mode is paused until 'continue' is passed.");
	                            // Clear any previously recorded clicks or TABs
	                            try {
	                                js.executeScript("document.querySelectorAll('[data-capture-click]').forEach(el => el.removeAttribute('data-capture-click'));");
	                                js.executeScript("document.body.removeAttribute('data-tab-pressed');");
	                            } catch (Exception e) {
	                                System.err.println("Error clearing capture attributes: " + e.getMessage());
	                            }
	                        }
	                        break;

	                    case "continue":
	                        String state = captureState.get();
	                        if (!"pause".equals(state) && !"start".equals(state)) {
	                            System.out.println("Please start the capture first using 'start'");
	                        } else {
	                            captureState.set("start");
	                            System.out.println("The XPath capturing mode is continued. Please proceed.");
	                            // Re-inject listeners and clear stale captures
	                            try {
	                                injectJavaScriptListeners.accept(js);
	                                js.executeScript("document.querySelectorAll('[data-capture-click]').forEach(el => el.removeAttribute('data-capture-click'));");
	                                js.executeScript("document.body.removeAttribute('data-tab-pressed');");
	                            } catch (Exception e) {
	                                System.err.println("Error re-injecting listeners: " + e.getMessage());
	                            }
	                        }
	                        break;

	                    case "stop":
	                        System.out.println("== XPath capture mode STOPPED ==");
	                        Operations.quitDriver(driver);
	                        return "STP001";

	                    case "dropdown":
	                        if (!"start".equals(captureState.get())) {
	                            System.out.println("Please start the capture first using 'start'");
	                        } else {
	                            Operations.getAllDynamicDropdownContainers_DynamicXpathGenerator();
	                            Operations.getAllPopUpDropdownContainers_DynamicXpathGenerator();
	                        }
	                        break;

	                    case "capture full":
	                        if (!"start".equals(captureState.get())) {
	                            System.out.println("Please start the capture first using 'start'");
	                        } else if (!fullCaptureRunning.get()) {
	                            System.out.println("Capturing all visible XPaths from page...");
	                            fullCaptureRunning.set(true);
	                            new Thread(() -> {
	                                try {
	                                    Operations.extractInputsWithLabels(driver);
	                                    Thread.sleep(300);
	                                    Operations.extractButtons(driver);
	                                    Thread.sleep(300);
	                                    Operations.extractCheckboxesAndRadioButtons(driver);
	                                    Thread.sleep(300);
	                                    Operations.extractDropdownsWithLabels(driver);
	                                    Thread.sleep(300);
	                                    Operations.extractNavigationElements(driver);
	                                    System.out.println("== Full page XPath capture COMPLETE ==");
	                                    System.out.println("== Please proceed with Click functionality or TAB Based Functionality or Capture Full ==");
	                                } catch (Exception e) {
	                                    System.err.println("Exception Raised during full capture: " + e);
	                                } finally {
	                                    fullCaptureRunning.set(false);
	                                }
	                            }).start();
	                        } else {
	                            System.out.println("Full capture is already in progress. Please wait...");
	                        }
	                        break;
	                        
	                    case "screenshot":
	                        Operations.captureFullPageScreenshots2(driver);
	                        System.out.println("------------- Screenshot Mode Exited-------------");
	                        break;
	                        
	                    default:
	                        if (!"start".equals(captureState.get())) {
	                            System.out.println("Please start the capture first using 'start'");
	                        } else if (!command.isEmpty()) {
	                            Operations.captureXPathByFieldName(driver, command);
	                        } else {
	                            System.out.println("Invalid command. Allowed: start, pause, continue, stop, dropdown, capture full or object name");
	                        }
	                        break;
	                }
	            }

	            Thread.sleep(1500);
	        }
	    } catch (StaleElementReferenceException staleEx) {
	        return "ERR003";
	    } catch (Exception e) {
	        return "ERR004";
	    }
	}
}