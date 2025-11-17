package Dynamic;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;
public class Operations {

	private static final String JSON_FILE_PATH = System.getProperty("user.dir")+"\\OR_Main.json";
	private static WebDriver m_driver;
	private static String targetURL = "";
	static int counter=findMaxCounterValue(JSON_FILE_PATH);
	static int datetimestampCounter = findMaxCounterValuedate(JSON_FILE_PATH);
	
    public static void reorderPageNameToTop(String jsonFilePath) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            File file = new File(jsonFilePath);
            if (!file.exists() || file.length() == 0) {
                System.out.println("File is empty or does not exist.");
                return;
            }

            ObjectNode rootNode = (ObjectNode) mapper.readTree(file);
            Iterator<Map.Entry<String, JsonNode>> fields = rootNode.fields();

            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String key = entry.getKey();
                JsonNode node = entry.getValue();

                if (node.has("page_name") && node.isObject()) {
                    ObjectNode original = (ObjectNode) node;
                    ObjectNode reordered = mapper.createObjectNode();

                    // Add "page_name" first
                    reordered.set("page_name", original.get("page_name"));

                    // Add all other fields except "page_name"
                    Iterator<Map.Entry<String, JsonNode>> subFields = original.fields();
                    while (subFields.hasNext()) {
                        Map.Entry<String, JsonNode> subEntry = subFields.next();
                        if (!subEntry.getKey().equals("page_name")) {
                            reordered.set(subEntry.getKey(), subEntry.getValue());
                        }
                    }

                    // Replace the node with the reordered one
                    rootNode.set(key, reordered);
                }
            }

            // Write back to file (or print)
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, rootNode);
            System.out.println("JSON successfully reordered.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static String derivePageName(String url, String title) {
        String pageName = url.replace("https://4423-tst3.btbanking.com/", "");
        pageName = pageName.toUpperCase().replace("/", " - ");
        pageName = pageName.equalsIgnoreCase("ui") ? "LOGIN PAGE" : pageName;
        pageName = title.contains("Home") ? "HOME PAGE" : pageName;
        return pageName;
    }
	
	public static void setTargetURL(String url) {
		targetURL = url;
	}
	 public String invokeBrowser() {
		try {

			System.setProperty("webdriver.chrome.silentOutput", "true");
			java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.SEVERE);
			Runtime.getRuntime().exec("Taskkill /F /IM chrome.exe /T");
			clearJsonFile();
			//System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+"\\resources\\libraries\\chromedriver.exe");

			m_driver = new ChromeDriver();
			m_driver.manage().window().maximize();
			//m_driver.get("https://4423-tst3.btbanking.com/ui"); // Set your actual URL
			m_driver.get("http://172.16.0.218:3000/auth/login");
			Thread.sleep(5500);
			return "0";
		} catch (Exception e) {
			e.printStackTrace();
			return "1";
		}
	}

//	 public String invokeBrowserUsingUrl(String Url) {
//			try {
//				
//				Thread.sleep(1200);
//				System.setProperty("webdriver.chrome.silentOutput", "true");
//				java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.SEVERE);
//				Runtime.getRuntime().exec("Taskkill /F /IM chrome.exe /T");
//				System.out.println("Do you want to delete existing OR Data - Enter Yes/No ?");
//				Scanner sc = new Scanner(System.in);
//				String delete = "";
//				delete = sc.nextLine().trim();
//
//				if(delete.equalsIgnoreCase("Yes"))
//				{
//					clearJsonFile();
//					counter=0;
//					datetimestampCounter=0;
//				}else {
//					System.out.println("You chose No, so the data is not deleted.");
//				}
//
//				System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+"\\resources\\libraries\\chromedriver.exe");
//
//				m_driver = new ChromeDriver();
//				m_driver.manage().window().maximize();
//				//m_driver.get("https://4423-tst3.btbanking.com/ui"); // Set your actual URL
//				m_driver.get(Url);
//				Thread.sleep(5500);
//				return "0";
//			} catch (Exception e) {
//				e.printStackTrace();
//				return "1";
//			}
//		}
	 public String invokeBrowserUsingUrl(String Url) {
		    try {
		        Thread.sleep(1200);
		        System.setProperty("webdriver.chrome.silentOutput", "true");
		        java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.SEVERE);

		        // Kill existing Chrome processes
		        Runtime.getRuntime().exec("Taskkill /F /IM chrome.exe /T");

		        // Load configuration
		        Properties config = new Properties();
		        config.load(new FileInputStream("config.properties"));

		        String clearJson = config.getProperty("clearORFile", "false").trim();

		        if (clearJson.equalsIgnoreCase("true") || clearJson.equalsIgnoreCase("yes")) {
		            clearJsonFile();
		            counter = 0;
		            datetimestampCounter = 0;
		            System.out.println("Previous Data from the OR is been cleared successfully.");
		        } else {
		            System.out.println("Previous Data from the OR is not cleared.");
		        }

		        // Launch Chrome
		        System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "\\resources\\libraries\\chromedriver.exe");

		        m_driver = new ChromeDriver();
		        m_driver.manage().window().maximize();
		        m_driver.get(Url);
		        Thread.sleep(5500);

		        return "0";

		    } catch (Exception e) {
		        e.printStackTrace();
		        return "1";
		    }
		}


	public static WebDriver getDriver() {
		return m_driver;
	}

	// Core method for extracting login page elements
	public static void extractAllLoginElements(WebDriver driver) {
		extractInputsWithLabels(driver);
		extractButtons(driver);
		extractCheckboxesAndRadioButtons(driver);
	}

	public static void extractInputsWithLabels_1(WebDriver driver) {
		List<WebElement> labels = driver.findElements(By.tagName("label"));

		for (WebElement label : labels) {
			//String labelText = label.getText().trim();
			String labelText = label.getText().replaceAll("[^\\p{L}\\p{N}\\s]", "").trim();

			if (!labelText.isEmpty()) {
				WebElement inputField = findInputField(label, driver);
				if (inputField != null) {
					saveElementToJson(labelText, inputField);
				}
			}
		}
	}

	public static WebElement findInputField(WebElement label, WebDriver driver) {
		try {
			String forAttr = label.getAttribute("for");
			if (forAttr != null && !forAttr.isEmpty()) {
				return driver.findElement(By.id(forAttr));
			}
			return label.findElement(By.xpath("following-sibling::input"));
		} catch (Exception ignored) {}
		return null;
	}

	public static void extractButtons(WebDriver driver) {
		try {
			// Collect all <button> and <input type='submit'|'button'>
			List<WebElement> buttons = driver.findElements(By.tagName("button"));
			buttons.addAll(driver.findElements(By.xpath("//input[@type='submit' or @type='button'] | //button" )));

			for (WebElement button : buttons) {
				if (!button.isDisplayed()) continue;

				String buttonText = button.getText().trim();
				if (buttonText.isEmpty()) {
					buttonText = button.getAttribute("value"); 
					//buttonText = button.getDomAttribute("value"); //// ---> MADE CHANGES 09/12/2025
				}

				if (!buttonText.isEmpty()) {
					String fieldName = "Button " + buttonText.replaceAll("\\s+", " ");

					// Special handling if ID is missing
					if ((button.getAttribute("id") == null || button.getAttribute("id").isEmpty()  )
							&& button.getTagName().equalsIgnoreCase("button")) { // ---> MADE CHANGES 09/12/2025
//						if ((button.getDomAttribute("id") == null || button.getDomAttribute("id").isEmpty() || button.getDomAttribute("text").isEmpty()  )
//								&& button.getTagName().equalsIgnoreCase("button")) {
						// Use visible text in XPath if ID is not present
						String tempXPath = "//button[text()='" + buttonText + "']";

						// Temporarily inject a dummy attribute for tracking if needed (optional)
						JavascriptExecutor js = (JavascriptExecutor) driver;
						js.executeScript("arguments[0].setAttribute('data-temp-xpath', 'true')", button);

						// Use this logic if you want to directly override the generated XPath (optional)
						// Or you can adjust `generateXPath()` method to consider this case.
					}

					// Save normally
					saveElementToJson(fieldName, button); 

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void extractCheckboxesAndRadioButtons(WebDriver driver) {
		List<WebElement> elements = driver.findElements(By.xpath("//input[@type='checkbox' or @type='radio']"));

		for (WebElement element : elements) {
			String name = element.getAttribute("name");
			String id = element.getAttribute("id");
			String value = element.getAttribute("value");
			String ariaLabel = element.getAttribute("aria-label");
			String checked = element.isSelected() ? "checked" : "unchecked";

			String fieldName;
			if (name != null && !name.isEmpty()) {
				fieldName = "Option " + name + " " + checked;
			} else if (id != null && !id.isEmpty()) {
				fieldName = "Option " + id + " " + checked;
			} else if (value != null && !value.isEmpty()) {
				fieldName = "Option " + value + " " + checked;
			} else if (ariaLabel != null && !ariaLabel.isEmpty()) {
				fieldName = "Option " + ariaLabel + " " + checked;
			} else {
				fieldName = "Unknown_Option_" + checked;
			}

			saveElementToJson(fieldName, element); 

		}
	}


	public static void saveElementToJson(String fieldName, WebElement element) 
	{
		try 
		{
			File file = new File(JSON_FILE_PATH);
			JSONObject jsonObject = file.exists() && file.length() > 0
					? new JSONObject(new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH))))
							: new JSONObject();

			String newXpath = generateXPath(element);
			String newId = element.getDomAttribute("id");
			String newTag = element.getTagName();
			String newType = element.getDomAttribute("type");

			JSONObject newFieldData = new JSONObject();
			newFieldData.put("xpath", newXpath);
			newFieldData.put("id", newId);
			newFieldData.put("tag", newTag);
			newFieldData.put("type", newType);

			boolean isDuplicate = false;
			if (jsonObject.has(fieldName)) {
				JSONObject existing = jsonObject.getJSONObject(fieldName);
				
				isDuplicate = existing.optString("xpath").equals(newXpath) &&
						existing.optString("id").equals(newId) &&
						existing.optString("tag").equals(newTag) &&
						existing.optString("type").equals(newType);
			}

			if (isDuplicate) {
				System.out.println("Skipping duplicate entry for: " + fieldName);
				return;
			}
			else
			{
				newFieldData = updateMetadata(fieldName,newFieldData,"Full Capture");
				// Update or Insert
				jsonObject.put(fieldName, newFieldData);
				try (FileWriter writer = new FileWriter(JSON_FILE_PATH)) {
					writer.write(jsonObject.toString(4));
				}
				sortJsonByClickCounterPreservingOthers();
				reorderPageNameToTop(JSON_FILE_PATH);

				System.out.println((jsonObject.has(fieldName) ? "Updated" : "Saved") + " XPath for: " + fieldName);
			}
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	public static void extractNavigationElements_OLD(WebDriver driver) {
		try {
			// Try to locate common nav bar containers
			List<WebElement> navBars = new ArrayList<>();
			navBars.addAll(driver.findElements(By.tagName("nav")));
			navBars.addAll(driver.findElements(By.tagName("header")));
			navBars.addAll(driver.findElements(By.cssSelector("[role='navigation']")));
			navBars.addAll(driver.findElements(By.xpath("//*[contains(@class, 'navbar') or contains(@class, 'nav')]")));

			Set<WebElement> collectedElements = new HashSet<>();

			for (WebElement navBar : navBars) {
				collectedElements.addAll(navBar.findElements(By.tagName("a")));       // links
				collectedElements.addAll(navBar.findElements(By.tagName("button")));  // buttons
				collectedElements.addAll(navBar.findElements(By.tagName("span")));    // spans
				collectedElements.addAll(navBar.findElements(By.tagName("li")));      // list items
				collectedElements.addAll(navBar.findElements(By.tagName("div")));     // divs
			}

			for (WebElement el : collectedElements) {
				String label = el.getText().trim();
				if (label.isEmpty()) label = el.getAttribute("aria-label");
				if (label == null || label.trim().isEmpty()) continue;

				// Clean label name
				label = label.replaceAll("[^a-zA-Z0-9\\s]", "").replaceAll("\\s+", "_");
				String fieldName = "Navbar_" + label;

				saveElementToJson(fieldName, el);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	public static String generateXPath(WebElement element) {
//		String tag = element.getTagName();
//		String id = element.getAttribute("id");
//		String name = element.getAttribute("name");
//		String type = element.getAttribute("type");
//		String text = element.getText().trim();
//
//		if (id != null && !id.isEmpty()) {
//			return "//" + tag + "[@id='" + id + "']";
//		} else if (!text.isEmpty() && (tag.equalsIgnoreCase("button") || tag.equalsIgnoreCase("a"))) {
//			return "//" + tag + "[text()='" + text + "']";
//		} else if (name != null && !name.isEmpty()) {
//			return "//" + tag + "[@name='" + name + "']";
//		} else if (type != null && !type.isEmpty()) {
//			return "//" + tag + "[@type='" + type + "']";
//		} else {
//			return "//" + tag;
//		}
//	}
	
	public static String generateXPath(WebElement element) {
	    String tag = element.getTagName();

	    List<String> dynamicAttributes = Arrays.asList("data-testid", "data-qa", "data-role");

	    // Collect attributes except ID
	    Map<String, String> attrMap = new LinkedHashMap<>();
	    attrMap.put("name", element.getDomProperty("name"));
	    attrMap.put("aria-label", element.getDomProperty("aria-label"));
	    attrMap.put("placeholder", element.getDomProperty("placeholder"));
	    attrMap.put("title", element.getDomProperty("title"));
	    attrMap.put("type", element.getDomProperty("type"));

	    for (String attr : dynamicAttributes) {
	        String value = element.getDomProperty(attr);
	        if (value != null && !value.isEmpty()) {
	            attrMap.put(attr, value);
	        }
	    }

	    String idValue = element.getDomProperty("id");

	    // Build attribute condition string
	    List<String> conditions = new ArrayList<>();
	    for (Map.Entry<String, String> entry : attrMap.entrySet()) {
	        String value = entry.getValue();
	        if (value != null && !value.isEmpty()) {
	            conditions.add("@" + entry.getKey() + "='" + value + "'");
	        }
	    }

	    StringBuilder xpathBuilder = new StringBuilder("//" + tag + "[");

	    if (!conditions.isEmpty()) {
	        xpathBuilder.append(String.join(" and ", conditions));
	    }
	    if (idValue != null && !idValue.isEmpty()) {
	        if (!conditions.isEmpty()) {
	            xpathBuilder.append(" or ");
	        }
	        xpathBuilder.append("@id='").append(idValue).append("'");
	    }

	    xpathBuilder.append("]");

	    String xpath = xpathBuilder.toString();
	    if(xpath.contains("@id"))
	    {
	    	xpath = xpath.replace("[", "[(");
	    	xpath = xpath.replace(" or ", ") or ");
	    }
	    // Ensure it is unique
	    if (m_driver.findElements(By.xpath(xpath)).size() == 1) {
	        return xpath;
	    }

	    // Fallback to text content for clickable tags
	    String text = element.getText().trim();
	    if (!text.isEmpty() && (tag.equalsIgnoreCase("button") || tag.equalsIgnoreCase("a") || tag.equalsIgnoreCase("span"))) {
	        String textXpath = "//" + tag + "[text()='" + text + "']";
	        if (m_driver.findElements(By.xpath(textXpath)).size() == 1) {
	            return textXpath;
	        }
	    }

	    // Final fallback
	    return "//" + tag;
	}


	public  static String extractNavigationElements(WebDriver driver) {
		try {
			Set<WebElement> navElements = new HashSet<>();

			// Collect navigation-related elements including li with title
			navElements.addAll(driver.findElements(By.xpath("//nav//* | //ul//li | //li[@title] | //header//*")));

			System.out.println("Found nav candidates: " + navElements.size());

			for (WebElement navItem : navElements) {
				try {
					if (!navItem.isDisplayed()) continue;

					String title = navItem.getAttribute("title");
					String text = navItem.getText().trim();

					// Skip if no identifying text or title
					if ((title == null || title.isEmpty()) && text.isEmpty()) continue;

					String objectName = "Nav_";
					if (title != null && !title.isEmpty()) {
						objectName += title.trim().replaceAll("\\s+", "_");
					} else {
						objectName += text.replaceAll("\\s+", "_");
					}

					// Save element with title-based name
					saveElementToJsonForNavigation(objectName, navItem); //Commenting 04212025

				} catch (Exception inner) {
					//inner.printStackTrace();
				}
			}

		} catch (Exception e) {
			return "ERR003";
			//e.printStackTrace();
		}
		return "0";
	}



	public static void saveElementToJsonForNavigation(String objectName, WebElement element) {
		try {
			String xpath = generateXPathNAVBAR(element);
			//String jsonFilePath = "C:\\ui-btdmvautomation\\TestxPath\\OR_Main.json";
			String jsonFilePath = JSON_FILE_PATH;

			File file = new File(jsonFilePath);
			JSONObject json;

			// Read existing JSON or create new
			if (file.exists()) {
				String content = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
				json = new JSONObject(content.isEmpty() ? "{}" : content);
			} else {
				json = new JSONObject();
			}

			if (!json.has(objectName)) {
				JSONObject data = new JSONObject();
				data.put("xpath", xpath);
				data.put("tag", element.getTagName());
				data.put("title", element.getAttribute("title"));
				data.put("text", element.getText().trim());
				data = updateMetadata(objectName,data,"Full Capture");
				json.put(objectName, data);

				try (FileWriter writer = new FileWriter(jsonFilePath)) {
					writer.write(json.toString(4));
					writer.flush();
					System.out.println("Navigation element saved: " + objectName);
				}
				sortJsonByClickCounterPreservingOthers();
				reorderPageNameToTop(JSON_FILE_PATH);
			} else {
				System.out.println("Navigation element already exists: " + objectName);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String generateXPathNAVBAR(WebElement element) {
		String tag = element.getTagName();
		String title = element.getAttribute("title");
		String text = element.getText().trim();

		// Priority 1: Use title attribute if available
		if (title != null && !title.isEmpty()) {
			return "//" + tag + "[@title='" + title + "']";
		}

		// Priority 2: Use text content if available
		if (text != null && !text.isEmpty()) {
			return "//" + tag + "[normalize-space(text())='" + text + "']";
		}

		// Fallback: Just tag (not recommended, but useful in edge cases)
		return "//" + tag;
	}

	public static void extractDropdownsWithLabels(WebDriver driver) {
		try {
			List<WebElement> labels = driver.findElements(By.tagName("label"));
			for (WebElement label : labels) {
				String labelText = label.getText().trim();
				labelText = labelText.replaceAll("[^\\p{L}\\p{N} ]", "").trim();

				if (labelText.isEmpty()) continue;

				String forAttr = label.getAttribute("for");
				if (forAttr != null && !forAttr.isEmpty()) {
					// Try to locate associated dropdown (usually a div with role='button' or select)
					WebElement dropdown = null;

					try {
						// Targeting combo-box like dropdowns
						dropdown = driver.findElement(By.xpath("//*[@id='" + forAttr + "']/ancestor::div[contains(@class, 'Mui')]"));
					} catch (Exception e) {
						// Optional: Log or ignore
					}

					if (dropdown != null) {
						JSONObject newFieldData = new JSONObject();
						String generatedXPath = generateDropdownXPath(dropdown, labelText);
						newFieldData.put("xpath", generatedXPath);
						newFieldData.put("id", ""); // Intentionally blank due to reused IDs
						newFieldData.put("tag", dropdown.getTagName());
						newFieldData.put("type", "dropdown");
						newFieldData.put("label", labelText);

						String objectName = "Dropdown_" + labelText.replaceAll("\\s+", "_");
						saveDropdownToJson(objectName, dropdown, labelText); // Existing save method that accepts JSONObject Comenting 04212025

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String generateDropdownXPath(WebElement dropdown, String labelText) {
		// Build XPath based on label's for attribute
		return "//label[text()='" + labelText + "']/following-sibling::*[1]";
	}

	public static void saveDropdownToJson(String fieldName, WebElement dropdown, String labelText) {
		try {
			File file = new File(JSON_FILE_PATH);
			JSONObject jsonObject = file.exists() && file.length() > 0
					? new JSONObject(new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH))))
							: new JSONObject();

			// XPath using label text
			String xpath = "//label[text()='" + labelText + "']/following-sibling::*[1]";
			String tag = dropdown.getTagName();

			JSONObject newFieldData = new JSONObject();
			newFieldData.put("xpath", xpath);
			newFieldData.put("id", ""); // Intentionally blank
			newFieldData.put("tag", tag);
			newFieldData.put("type", "dropdown");
			newFieldData.put("label", labelText);

			// Check for duplication based on XPath
			boolean isDuplicate = false;
			if (jsonObject.has(fieldName)) {
				JSONObject existing = jsonObject.getJSONObject(fieldName);
				isDuplicate = existing.optString("xpath").equals(xpath);
			}

			if (isDuplicate) {
				System.out.println("Skipping duplicate dropdown for: " + fieldName);
				return;
			}
			else
			{
				newFieldData = updateMetadata(fieldName,newFieldData,"Full Capture");
				jsonObject.put(fieldName, newFieldData);
				try (FileWriter writer = new FileWriter(JSON_FILE_PATH)) {
					writer.write(jsonObject.toString(4));
				}
				sortJsonByClickCounterPreservingOthers();
				reorderPageNameToTop(JSON_FILE_PATH);

				System.out.println("Saved dropdown for: " + fieldName);
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void clearJsonFile() {
		try (FileWriter fileWriter = new FileWriter(JSON_FILE_PATH)) {
			fileWriter.write(""); // Empty JSON object
			System.out.println("OR_Main.json cleared before browser launch.");
		} catch (IOException e) {
			System.err.println("Failed to clear OR_Main.json:");
			e.printStackTrace();
		}
	}

	//CODE WORKING : OLD
	public static void clickAndCapture_OLD1(WebElement element, WebDriver driver) {
		try {
			String tag = element.getTagName();
			String id = element.getAttribute("id");
			String name = element.getAttribute("name");
			String type = element.getAttribute("type");
			String text = element.getText().trim();

			String xpath;
			if (id != null && !id.isEmpty()) {
				xpath = "//*[@" + "id='" + id + "']";
			} else if (!text.isEmpty()) {
				xpath = "//" + tag + "[text()='" + text + "']";
			} else {
				xpath = "//" + tag;
			}

			String objectName = "Clicked_" + tag + "_" + (text.isEmpty() ? (id != null ? id : "unknown") : text.replaceAll("\\s+", "_"));
			System.out.println("Captured XPath: " + xpath);

			saveElementToJson(objectName, element);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	private static void saveElementToJsonConsole(String fieldName, JSONObject newFieldData) {
		try {
			File file = new File(JSON_FILE_PATH);
			JSONObject jsonObject = file.exists() && file.length() > 0
					? new JSONObject(new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH))))
							: new JSONObject();

			boolean isDuplicate = false;
			if (jsonObject.has(fieldName)) {
				JSONObject existing = jsonObject.getJSONObject(fieldName);

				isDuplicate = existing.optString("xpath").equals(newFieldData.optString("xpath")) &&
						existing.optString("id").equals(newFieldData.optString("id")) &&
						existing.optString("tag").equals(newFieldData.optString("tag")) &&
						existing.optString("type").equals(newFieldData.optString("type"));
			}

			if (isDuplicate) {
				System.out.println("Skipping duplicate entry for: " + fieldName);
				return;
			}
			else
			{
				newFieldData = updateMetadata(fieldName, newFieldData, "Full Capture");
				jsonObject.put(fieldName, newFieldData);
				try (FileWriter writer = new FileWriter(JSON_FILE_PATH)) {
					writer.write(jsonObject.toString(4));
				}
				sortJsonByClickCounterPreservingOthers();
				reorderPageNameToTop(JSON_FILE_PATH);
				System.out.println((jsonObject.has(fieldName) ? "Updated" : "Saved") + " XPath for: " + fieldName);
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void clickAndCapture(WebElement element, WebDriver driver) {
		try {
			String tag = element.getTagName();
			String id = element.getAttribute("id");
			String type = element.getAttribute("type");
			String text = element.getText().trim();
			String nameAttr = element.getAttribute("name");

			// Clean special characters like non-breaking spaces
			text = text.replaceAll("[^\\p{Print}\\p{L}\\p{N}\\s]", "").trim();

			// Attempt to get label text for input fields
			if ((tag.equals("input") || tag.equals("select") || tag.equals("textarea")) && (text == null || text.isEmpty())) {
				text = getLabelTextForInput(element, driver);
			}

			// Fallback if text is still empty
			if (text == null || text.isEmpty()) {
				text = nameAttr != null ? nameAttr : (id != null ? id : "unknown");
			}

			String objectName = text.replaceAll("\\s+", " ");

			// Create XPath
			String xpath;
			if (id != null && !id.isEmpty()) {
				xpath = "//*[@" + "id='" + id + "']";
			} else if (!text.isEmpty()) {
				xpath = "//" + tag + "[text()='" + text + "']";
			} else {
				xpath = "//" + tag;
			}

			System.out.println("Captured XPath: " + xpath);

			JSONObject json = new JSONObject();
			json.put("xpath", xpath);
			json.put("id", id != null ? id : "");
			json.put("tag", tag);
			json.put("type", type != null ? type : "");
			json.put("title", text);

			saveElementToJsonConsole(objectName, json);
			//saveElementToJson(objectName, json);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static String getLabelTextForInput(WebElement element, WebDriver driver) {
		try {
			String id = element.getAttribute("id");
			if (id != null && !id.isEmpty()) {
				WebElement label = driver.findElement(By.cssSelector("label[for='" + id + "']"));
				String labelText = label.getText().replaceAll("[^\\p{Print}\\p{L}\\p{N}\\s]", "").trim();
				return labelText;
			}
		} catch (Exception ignored) {}
		return "";
	}


	public static void extractInputsWithLabels(WebDriver driver) {
		List<WebElement> labels = driver.findElements(By.tagName("label"));

		for (WebElement label : labels) {
			try {
				String rawLabel = label.getText().trim();

				// Dynamically clean label (remove symbols, invisible chars, extra whitespace)
				String cleanedLabel = rawLabel.replaceAll("[^\\p{L}\\p{Nd}\\s]", "").replaceAll("\\s+", " ").trim();

				WebElement input = findInputField(label, driver);

				if (input != null && !cleanedLabel.isEmpty()) {
					String xpath = generateXPath(input);

					JSONObject json = new JSONObject();
					json.put("xpath", xpath);
					json.put("id", ""); // Intentionally left blank as IDs may be reused
					json.put("tag", input.getTagName());
					json.put("type", input.getAttribute("type"));
					json.put("title", input.getAttribute("title"));
					
					saveElementToJsonConsole(cleanedLabel, json); 

					System.out.println("Saved input for: " + cleanedLabel);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/** //NEW functions added below  **/
	//NEW functions added below 
	public static String cleanFieldName(String raw) {
		return raw.replaceAll("[^\\p{L}\\p{N}\\s]", "") // Remove symbols like * etc.
				.replaceAll("\\s+", "_")              // Replace spaces with _
				.trim();
	}

	public static String getLabelTextForElement(WebElement element, WebDriver driver) {
		try {
			String id = element.getAttribute("id");
			if (id != null && !id.isEmpty()) {
				List<WebElement> labels = driver.findElements(By.xpath("//label[@for='" + id + "']"));
				if (!labels.isEmpty()) {
					return labels.get(0).getText().trim();
				}
			}

			// Fallback to label parent
			WebElement parent = element.findElement(By.xpath("ancestor::label"));
			if (parent != null) {
				return parent.getText().trim();
			}

		} catch (Exception e) {
			return "";
		}
		return "";
	}
	public static void clickAndCapture2(WebElement element, WebDriver driver) {
		try {
			JavascriptExecutor js = (JavascriptExecutor) driver;

			// Traverse up to find a better clickable ancestor
			WebElement target = element;
			String tag = element.getTagName();

			if (tag.equalsIgnoreCase("span") || tag.equalsIgnoreCase("i")) {
				WebElement parent = (WebElement) js.executeScript("return arguments[0].closest('button, a, div')", element);
				if (parent != null) {
					target = parent;
				}
			}

			// Now use the target for extraction
			String finalTag = target.getTagName();
			String id = target.getAttribute("id");
			String name = target.getAttribute("name");
			String type = target.getAttribute("type");
			String className = target.getAttribute("class");
			String text = target.getText().trim();
			String title = target.getAttribute("title");

			String xpath;

			if (id != null && !id.isEmpty()) {
				xpath = "//*[@" + "id='" + id + "']";
			} else if (className != null && !className.isEmpty()) {
				className = className.replaceAll("\\s+", " ").trim();
				xpath = "//" + finalTag + "[@class='" + className + "']";
			} else if (!text.isEmpty()) {
				xpath = "//" + finalTag + "[text()='" + text + "']";
			} else if (name != null && !name.isEmpty()) {
				xpath = "//" + finalTag + "[@name='" + name + "']";
			} else {
				xpath = "//" + finalTag;
			}

			String fieldName = !text.isEmpty() ? text :
				!title.isEmpty() ? title :
					!name.isEmpty() ? name :
						!id.isEmpty() ? id :
							!className.isEmpty() ? className : finalTag;

			fieldName = fieldName.replaceAll("[^a-zA-Z0-9_ ]", "").replaceAll("\\s+", " ").trim();
			if (fieldName.isEmpty()) fieldName = "Unnamed_" + finalTag;

			System.out.println("Captured XPath: " + xpath);
			System.out.println("Field Name: " + fieldName);

			saveElementToJson(fieldName, target);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//ADDED NEW FUNCTION 04/16/2025
	public static void clickAndCaptureNew1(WebElement element, WebDriver driver) {
		try {
			JavascriptExecutor js = (JavascriptExecutor) driver;

			// If a span or icon is clicked, go up to the clickable parent
			WebElement target = element;
			String tag = element.getTagName();

			if (tag.equalsIgnoreCase("span") || tag.equalsIgnoreCase("i")) {
				WebElement parent = (WebElement) js.executeScript("return arguments[0].closest('button, a, div')", element);
				if (parent != null) {
					target = parent;
				}
			}

			String id = safeAttr(target, "id");
			String name = safeAttr(target, "name");
			String type = safeAttr(target, "type");
			String className = safeAttr(target, "class");
			String text = safeText(target);
			String title = safeAttr(target, "title");
			String tagName = target.getTagName();

			String xpath;

			// Updated XPath logic:
			if (!id.isEmpty()) {
				xpath = "//*[@" + "id='" + id + "']";
			} else if (!className.isEmpty()) {
				className = className.replaceAll("\\s+", " ").trim();
				xpath = "//" + tagName + "[@class='" + className + "']";
			} else if (!type.isEmpty()) {
				xpath = "//" + tagName + "[@type='" + type + "']";
			} else if (!text.isEmpty()) {
				xpath = "//" + tagName + "[text()='" + text + "']";
			} else if (!name.isEmpty()) {
				xpath = "//" + tagName + "[@name='" + name + "']";
			} else {
				xpath = "//" + tagName;
			}

			// Clean object name for the JSON
			String fieldName = !text.isEmpty() ? text :
				!title.isEmpty() ? title :
					!name.isEmpty() ? name :
						!id.isEmpty() ? id :
							!className.isEmpty() ? className :
								!type.isEmpty() ? type :
									tagName;

			fieldName = fieldName.replaceAll("css-[a-zA-Z0-9]+", "")  // remove dynamic class suffixes
					.replaceAll("[^a-zA-Z0-9_ ]", "")
					.replaceAll("\\s+", " ")
					.trim();

			if (fieldName.isEmpty()) fieldName = "Unnamed_" + tagName;

			System.out.println("Captured XPath: " + xpath);
			System.out.println("Field Name: " + fieldName);

			saveElementToJson2(fieldName, target);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String safeAttr(WebElement el, String attr) {
		try {
			String val = el.getAttribute(attr);
			return val != null ? val.trim() : "";
		} catch (Exception e) {
			return "";
		}
	}

	private static String safeText(WebElement el) {
		try {
			String val = el.getText();
			return val != null ? val.trim() : "";
		} catch (Exception e) {
			return "";
		}
	}



	public static void saveElementToJson2(String fieldName, WebElement element) {
		try {
			File file = new File(JSON_FILE_PATH);
			JSONObject jsonObject = file.exists() && file.length() > 0
					? new JSONObject(new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH))))
							: new JSONObject();

			String newXpath = generateXPath(element);
			String newId = element.getAttribute("id");
			String newTag = element.getTagName();
			String newType = element.getAttribute("type");

			JSONObject newFieldData = new JSONObject();
			newFieldData.put("xpath", newXpath);
			newFieldData.put("id", newId != null ? newId : "");
			newFieldData.put("tag", newTag != null ? newTag : "");
			newFieldData.put("type", newType != null ? newType : "");

			boolean isDuplicate = jsonObject.has(fieldName) &&
					jsonObject.getJSONObject(fieldName).toString().equals(newFieldData.toString());

			if (isDuplicate) {
				System.out.println("Skipping duplicate entry for: " + fieldName);
				return;
			}

			jsonObject.put(fieldName, newFieldData);
			try (FileWriter writer = new FileWriter(JSON_FILE_PATH)) {
				writer.write(jsonObject.toString(4));
			}

			System.out.println("Saved XPath for: " + fieldName);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Utility to safely get attributes
	private static String getSafeAttr(WebElement el, String attr) {
		try {
			if (attr.equals("textContent")) return el.getText();
			String val = el.getAttribute(attr);
			return val != null ? val : "";
		} catch (Exception e) {
			return "";
		}
	}


	private static String safeGet(Supplier<String> supplier) {
		try {
			String value = supplier.get();
			return value != null ? value : "";
		} catch (Exception e) {
			return "";
		}
	}

	public static void clickAndCaptureFinal(WebElement element, WebDriver driver) {
		try {	//Not working properly
			JavascriptExecutor js = (JavascriptExecutor) driver;

			// Step 1: Traverse up if span or i is clicked
			WebElement target = element;
			String tag = element.getTagName();

			if (tag.equalsIgnoreCase("span") || tag.equalsIgnoreCase("i")) {
				WebElement parent = (WebElement) js.executeScript("return arguments[0].closest('button, a, div')", element);
				if (parent != null) {
					target = parent;
				}
			}

			// Step 2: Get all relevant attributes safely
			String finalTag = target.getTagName();
			String id = getSafeAttr(target, "id");
			String name = getSafeAttr(target, "name");
			String type = getSafeAttr(target, "type");
			String className = getSafeAttr(target, "class");
			String text = getSafeAttr(target, "textContent").trim();
			String title = getSafeAttr(target, "title");

			// Step 3: Generate XPath with proper priority
			String xpath;
			if (!id.isEmpty()) {
				xpath = "//*[@" + "id='" + id + "']";
			} else if (!text.isEmpty()) {
				xpath = "//" + finalTag + "[normalize-space(text())='" + text + "']";
			} else if (!type.isEmpty()) {
				xpath = "//" + finalTag + "[@type='" + type + "']";
			} else if (!className.isEmpty() && !className.matches(".*css-[a-zA-Z0-9]+.*")) {
				xpath = "//" + finalTag + "[@class='" + className + "']";
			} else {
				xpath = "//" + finalTag;
			}

			// Step 4: Field name (cleaned)
			String fieldName = !text.isEmpty() ? text :
				!title.isEmpty() ? title :
					!name.isEmpty() ? name :
						!id.isEmpty() ? id :
							!className.isEmpty() ? className :
								finalTag;

			fieldName = fieldName.replaceAll("[^\\p{L}\\p{N}_ ]", "").replaceAll("\\s+", " ").trim();
			if (fieldName.isEmpty()) fieldName = "Unnamed_" + finalTag;

			// Debug print
			System.out.println("Captured XPath: " + xpath);
			System.out.println("Field Name: " + fieldName);

			// Step 5: Save to JSON
			saveElementToJson2(fieldName, target);

		} catch (StaleElementReferenceException staleEx) {
			System.err.println("Stale element! Try clicking again.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//Till now this function is working properly.
	public static void clickAndCaptureNewNative(WebElement element, WebDriver driver) {
		try {
			JavascriptExecutor js = (JavascriptExecutor) driver;

			// Default element to capture
			WebElement target = element;
			String tag = element.getTagName();

			// Traverse up for clickable elements like <span> or <i>
			if (tag.equalsIgnoreCase("span") || tag.equalsIgnoreCase("i") || tag.equalsIgnoreCase("button")) {
				try {
					WebElement parent = (WebElement) js.executeScript("return arguments[0].closest('button, a, div')", element);
					if (parent != null) {
						target = parent;
					}
				} catch (Exception ignored) {}
			}

			// Capture safely
			String finalTag = safeGetTagName(target);
			String id = safeGetAttribute(target, "id");
			String name = safeGetAttribute(target, "name");
			String type = safeGetAttribute(target, "type");
			String className = cleanClass(safeGetAttribute(target, "class"));
			String text = safeGetText(target);
			String title = safeGetAttribute(target, "title");

			// Construct XPath - Priority: id > class > type > name > text
			String xpath;
			if (!id.isEmpty()) {
				xpath = "//*[@" + "id='" + id + "']";
			} else if (!className.isEmpty()) {
				xpath = "//" + finalTag + "[@class='" + className + "']";
			} else if (!type.isEmpty()) {
				xpath = "//" + finalTag + "[@type='" + type + "']";
			} else if (!name.isEmpty()) {
				xpath = "//" + finalTag + "[@name='" + name + "']";
			} else if (!text.isEmpty()) {
				xpath = "//" + finalTag + "[normalize-space(text())='" + text + "']";
			} else {
				xpath = "//" + finalTag;
			}

			// Determine field name
			String rawFieldName = !text.isEmpty() ? text :
				!title.isEmpty() ? title :
					!name.isEmpty() ? name :
						!id.isEmpty() ? id :
							!className.isEmpty() ? className : finalTag;

			String fieldName = cleanFieldName(rawFieldName, finalTag);

			System.out.println("Captured XPath: " + xpath);
			System.out.println("Field Name: " + fieldName);

			// Save to JSON
			saveElementToJsonUsingClickNative(fieldName, target);
			//saveElementToJsonUsingClick(fieldName, target); //Commented for temporary purpose
			//saveElementToJsonConsole(fieldName, target); //not to use
		} catch (StaleElementReferenceException staleEx) {
			System.out.println("Element is no longer attached to the DOM. Skipping...");
		} catch (Exception e) {

			//e.printStackTrace();
		}
	}


	public static String safeGetAttribute(WebElement element, String attr) {
		try {
			String val = element.getAttribute(attr);
			return val != null ? val.trim() : "";
		} catch (Exception e) {
			return "";
		}
	}

	public static String safeGetText(WebElement element) {
		try {
			String val = element.getText();
			return val != null ? val.trim() : "";
		} catch (Exception e) {
			return "";
		}
	}

	public static String safeGetTagName(WebElement element) {
		try {
			return element.getTagName().trim();
		} catch (Exception e) {
			return "element";
		}
	}

	public static String cleanClass(String classValue) {
		return classValue.replaceAll("\\s+", " ")
				.replaceAll("css-[a-zA-Z0-9]+", "")
				.trim();
	}

	public static String cleanFieldName(String raw, String fallbackTag) {
		String cleaned = raw.replaceAll("[^a-zA-Z0-9_ ]", "")
				.replaceAll("\\s+", " ")
				.trim();
		return cleaned.isEmpty() ? "Unnamed_" + fallbackTag : cleaned;
	}


	public static void clickVerifyandExecute(WebElement element, WebDriver driver) {
		try {
			String tag = element.getTagName().toLowerCase();
			String type = element.getAttribute("type") != null ? element.getAttribute("type").toLowerCase() : "";
			String role = element.getAttribute("role") != null ? element.getAttribute("role").toLowerCase() : "";
			String className = element.getAttribute("class") != null ? element.getAttribute("class").toLowerCase() : "";

			if (tag.equals("input") && (type.equals("checkbox") || type.equals("radio"))) {
				System.out.println("inside checkbox");
				Operations.extractCheckboxesAndRadioButtons(driver);
			} else if (tag.equals("select") || className.contains("dropdown") || role.contains("combobox")) {
				System.out.println("inside dropdown");
				Operations.extractDropdownsWithLabels(driver);
			} else if (tag.equals("button") || type.equals("submit") || className.contains("btn") || role.contains("button")) {
				System.out.println("inside button");
				Operations.extractButtons(driver);
			} else if (className.contains("nav") || className.contains("menu") || role.contains("navigation")) {
				System.out.println("inside Navi");
				Operations.extractNavigationElements(driver);
			} else {
				// Default fallback for inputs
				Operations.extractInputsWithLabels(driver);
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error in clickVerifyandExecute while identifying element type.");
		}
	}

	public static void saveElementToJsonUsingClick(String fieldName, WebElement element) {
		try {
			//String JSON_FILE_PATH = "OR_Main.json";
			File file = new File(JSON_FILE_PATH);

			JSONObject jsonObject = file.exists() && file.length() > 0
					? new JSONObject(new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH))))
							: new JSONObject();

			String xpath = generateXPathUsingClick(element);
			String id = element.getAttribute("id");
			String tag = element.getTagName();
			String type = element.getAttribute("type");

			JSONObject newObj = new JSONObject();
			newObj.put("xpath", xpath);
			newObj.put("id", (id != null) ? id : "");
			newObj.put("tag", (tag != null) ? tag : "");
			newObj.put("type", (type != null) ? type : "");

			if (!jsonObject.has(fieldName)) {
				jsonObject.put(fieldName, newObj);

				try (FileWriter writer = new FileWriter(JSON_FILE_PATH)) {
					writer.write(jsonObject.toString(4));

					writer.flush();
				}

				System.out.println("Element saved: " + fieldName);
			} else {
				System.out.println(" Field already exists: " + fieldName);
			}

		} catch (Exception e) {
			System.err.println(" Failed to save element: " + fieldName);
			e.printStackTrace();
		}
	}

	

	//New Added : 04172025


	public static void saveElementToJsonUsingClickNative(String fieldName, WebElement element) {
		
		try {
			File file = new File(JSON_FILE_PATH);
			ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

			// Load existing data
			LinkedHashMap<String, Object> orderedMap = new LinkedHashMap<>();
			if (file.exists() && file.length() > 0) {
				orderedMap = mapper.readValue(file, LinkedHashMap.class);
			}

			// Only insert if not already present
			if (!orderedMap.containsKey(fieldName)) {
				LinkedHashMap<String, String> entry = new LinkedHashMap<>();
				entry.put("xpath", generateXPathUsingClick(element));
				entry.put("id", Optional.ofNullable(element.getAttribute("id")).orElse(""));
				entry.put("tag", Optional.ofNullable(element.getTagName()).orElse(""));
				entry.put("type", Optional.ofNullable(element.getAttribute("type")).orElse(""));
				JSONObject obj = new JSONObject(entry);
				obj = updateMetadata(fieldName, obj, "Mouse Click");
				entry = convertJsonObjectToMap(obj);
				orderedMap.put(fieldName, entry);
				// Save to JSON file with insertion order
				mapper.writeValue(file, orderedMap);
				sortJsonByClickCounterPreservingOthers();
				reorderPageNameToTop(JSON_FILE_PATH);
				System.out.println("Element saved: " + fieldName);
			} else {
				System.out.println("Field already exists: " + fieldName);
			}

		} catch (Exception e) {
			System.err.println("Failed to save element: " + fieldName);
			//e.printStackTrace();
		}
	}

	public static LinkedHashMap<String, String> convertJsonObjectToMap(JSONObject jsonObject) {
	    LinkedHashMap<String, String> map = new LinkedHashMap<>();
	    Iterator<String> keys = jsonObject.keys();

	    while (keys.hasNext()) {
	        String key = keys.next();
	        map.put(key, jsonObject.optString(key));
	    }

	    return map;
	}
	//Newly added functions 04/21/2025
	/*
	private static void saveElementToJsonConsoleNative(String fieldName, JSONObject newFieldData) {
	    try {
	        File file = new File(JSON_FILE_PATH);

	        // Load existing data into a LinkedHashMap to preserve order
	        LinkedHashMap<String, Object> orderedMap = new LinkedHashMap<>();
	        if (file.exists() && file.length() > 0) {
	            String content = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH)));
	            JSONObject existing = new JSONObject(content);
	            Iterator<String> keys = existing.keys();
	            while (keys.hasNext()) {
	                String key = keys.next();
	                orderedMap.put(key, existing.getJSONObject(key));
	            }
	        }

	        // Check for duplicate before inserting
	        if (orderedMap.containsKey(fieldName)) {
	            JSONObject existing = new JSONObject(orderedMap.get(fieldName).toString());
	            boolean isDuplicate =
	                    existing.optString("xpath").equals(newFieldData.optString("xpath")) &&
	                    existing.optString("id").equals(newFieldData.optString("id")) &&
	                    existing.optString("tag").equals(newFieldData.optString("tag")) &&
	                    existing.optString("type").equals(newFieldData.optString("type"));

	            if (isDuplicate) {
	                System.out.println("Skipping duplicate entry for: " + fieldName);
	                return;
	            }
	        }

	        orderedMap.put(fieldName, newFieldData); // Add or update

	        // Write back in insertion order
	        JSONObject finalJson = new JSONObject(orderedMap);
	        try (FileWriter writer = new FileWriter(JSON_FILE_PATH)) {
	            writer.write(finalJson.toString(4));
	        }

	        System.out.println("Element saved: " + fieldName);

	    } catch (Exception e) {
	        System.err.println("Failed to save element: " + fieldName);
	        e.printStackTrace();
	    }
	}

	public static void saveElementToJsonNative(String fieldName, WebElement element) {
	    try {
	        File file = new File(JSON_FILE_PATH);

	        // Use LinkedHashMap to preserve insertion order
	        LinkedHashMap<String, Object> orderedMap = new LinkedHashMap<>();

	        if (file.exists() && file.length() > 0) {
	            String content = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH)));
	            JSONObject existing = new JSONObject(content);
	            Iterator<String> keys = existing.keys();
	            while (keys.hasNext()) {
	                String key = keys.next();
	                orderedMap.put(key, existing.getJSONObject(key));
	            }
	        }

	        String newXpath = generateXPath(element);
	        String newId = element.getAttribute("id");
	        String newTag = element.getTagName();
	        String newType = element.getAttribute("type");

	        JSONObject newFieldData = new JSONObject();
	        newFieldData.put("xpath", newXpath);
	        newFieldData.put("id", (newId != null) ? newId : "");
	        newFieldData.put("tag", (newTag != null) ? newTag : "");
	        newFieldData.put("type", (newType != null) ? newType : "");

	        if (orderedMap.containsKey(fieldName)) {
	            JSONObject existing = new JSONObject(orderedMap.get(fieldName).toString());

	            boolean isDuplicate =
	                    existing.optString("xpath").equals(newXpath) &&
	                    existing.optString("id").equals(newId) &&
	                    existing.optString("tag").equals(newTag) &&
	                    existing.optString("type").equals(newType);

	            if (isDuplicate) {
	                System.out.println("Skipping duplicate entry for: " + fieldName);
	                return;
	            }
	        }

	        orderedMap.put(fieldName, newFieldData); // Add or update

	        JSONObject finalJson = new JSONObject(orderedMap);
	        try (FileWriter writer = new FileWriter(JSON_FILE_PATH)) {
	            writer.write(finalJson.toString(4));
	        }

	        System.out.println("Element saved: " + fieldName);

	    } catch (Exception e) {
	        System.err.println("Failed to save element: " + fieldName);
	        e.printStackTrace();
	    }
	}

	public static void saveDropdownToJsonNative(String fieldName, WebElement dropdown, String labelText) {
	    try {
	        File file = new File(JSON_FILE_PATH);

	        // Preserve insertion order
	        LinkedHashMap<String, Object> orderedMap = new LinkedHashMap<>();

	        if (file.exists() && file.length() > 0) {
	            String content = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH)));
	            JSONObject existing = new JSONObject(content);
	            Iterator<String> keys = existing.keys();
	            while (keys.hasNext()) {
	                String key = keys.next();
	                orderedMap.put(key, existing.getJSONObject(key));
	            }
	        }

	        // XPath using label text
	        String xpath = "//label[text()='" + labelText + "']/following-sibling::*[1]";
	        String tag = dropdown.getTagName();

	        JSONObject newFieldData = new JSONObject();
	        newFieldData.put("xpath", xpath);
	        newFieldData.put("id", ""); // Intentionally blank
	        newFieldData.put("tag", (tag != null) ? tag : "");
	        newFieldData.put("type", "dropdown");
	        newFieldData.put("label", labelText);

	        if (orderedMap.containsKey(fieldName)) {
	            JSONObject existing = new JSONObject(orderedMap.get(fieldName).toString());

	            boolean isDuplicate = existing.optString("xpath").equals(xpath);

	            if (isDuplicate) {
	                System.out.println("Skipping duplicate dropdown for: " + fieldName);
	                return;
	            }
	        }

	        orderedMap.put(fieldName, newFieldData); // Add or update

	        JSONObject finalJson = new JSONObject(orderedMap);
	        try (FileWriter writer = new FileWriter(JSON_FILE_PATH)) {
	            writer.write(finalJson.toString(4));
	        }

	        System.out.println("Saved dropdown for: " + fieldName);

	    } catch (Exception e) {
	        System.err.println("Failed to save dropdown for: " + fieldName);
	        e.printStackTrace();
	    }
	}

	public static void saveElementToJsonForNavigationNative(String objectName, WebElement element) {
	    try {
	        String xpath = generateXPathNAVBAR(element);
	        File file = new File(JSON_FILE_PATH);

	        // Use LinkedHashMap to maintain insertion order
	        LinkedHashMap<String, Object> orderedMap = new LinkedHashMap<>();

	        if (file.exists() && file.length() > 0) {
	            String content = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH)));
	            JSONObject existing = new JSONObject(content);
	            Iterator<String> keys = existing.keys();
	            while (keys.hasNext()) {
	                String key = keys.next();
	                orderedMap.put(key, existing.getJSONObject(key));
	            }
	        }

	        if (!orderedMap.containsKey(objectName)) {
	            JSONObject data = new JSONObject();
	            data.put("xpath", xpath);
	            data.put("tag", element.getTagName());
	            data.put("title", element.getAttribute("title"));
	            data.put("text", element.getText().trim());

	            orderedMap.put(objectName, data);

	            JSONObject finalJson = new JSONObject(orderedMap);
	            try (FileWriter writer = new FileWriter(JSON_FILE_PATH)) {
	                writer.write(finalJson.toString(4));
	                writer.flush();
	                System.out.println("Navigation element saved: " + objectName);
	            }
	        } else {
	            System.out.println("Navigation element already exists: " + objectName);
	        }

	    } catch (Exception e) {
	        System.err.println("Failed to save navigation element: " + objectName);
	        e.printStackTrace();
	    }
	}
	 */

	public static String clickAndCaptureNewNative2(WebElement element, WebDriver driver) {
		try {
			JavascriptExecutor js = (JavascriptExecutor) driver;

			// Default element to capture
			WebElement target = element;
			String tag = element.getTagName();

			// Traverse up for clickable elements like <span> or <i>
			if (tag.equalsIgnoreCase("span") || tag.equalsIgnoreCase("i") || tag.equalsIgnoreCase("button")) {
				try {
					WebElement parent = (WebElement) js.executeScript("return arguments[0].closest('button, a, div')", element);
					if (parent != null) {
						target = parent;
					}
				} catch (Exception ignored) {}
			}

			// Capture safely
			String finalTag = safeGetTagName(target);
			String id = safeGetAttribute(target, "id");
			String name = safeGetAttribute(target, "name");
			String type = safeGetAttribute(target, "type");
			String className = cleanClass(safeGetAttribute(target, "class"));
			String text = safeGetText(target);
			String title = safeGetAttribute(target, "title");

			// Construct XPath - Priority: id > class > type > name > text
			String xpath;
			if (!id.isEmpty()) {
				xpath = "//*[@" + "id='" + id + "']";
			} else if (!className.isEmpty()) {
				xpath = "//" + finalTag + "[@class='" + className + "']";
			} else if (!type.isEmpty()) {
				xpath = "//" + finalTag + "[@type='" + type + "']";
			} else if (!name.isEmpty()) {
				xpath = "//" + finalTag + "[@name='" + name + "']";
			} else if (!text.isEmpty()) {
				xpath = "//" + finalTag + "[normalize-space(text())='" + text + "']";
			} else {
				xpath = "//" + finalTag;
			}

			// Determine field name
			String rawFieldName = !text.isEmpty() ? text :
				!title.isEmpty() ? title :
					!name.isEmpty() ? name :
						!id.isEmpty() ? id :
							!className.isEmpty() ? className : finalTag;

			String fieldName = cleanFieldName(rawFieldName, finalTag);

			System.out.println("Captured XPath: " + xpath);
			System.out.println("Field Name: " + fieldName);

			// Save to JSON
			saveElementToJsonUsingClickNative(fieldName, target);
			//saveElementToJsonUsingClick(fieldName, target); //Commented for temporary purpose
			//saveElementToJsonConsole(fieldName, target); //not to use
		} catch (StaleElementReferenceException staleEx) {
			//System.out.println("Element is no longer attached to the DOM. Skipping...");
			return "-1";
		} catch (Exception e) {
			return "-1";
		}
		return "0";
	}



	// Working Functions - 05142025
	/*
	public static String getXPathOfElementUsingTAB(WebDriver driver, WebElement element) {
	    try {
	        String tag = element.getTagName();
	        String id = element.getAttribute("id");
	        if (id != null && !id.isEmpty()) {
	            return "//*[@" + "id='" + id + "']";
	        }

	        // Fallback XPath generation
	        return generateXPathUsingTAB(element); // You already use this in your click flow
	    } catch (Exception e) {
	        return "UNKNOWN_XPATH";
	    }
	}

	public static String generateXPathUsingTAB(WebElement element) {
	    String tag = element.getTagName();
	    String id = element.getAttribute("id");
	    String classAttr = element.getAttribute("class");
	    String typeAttr = element.getAttribute("type");

	    if (id != null && !id.isEmpty()) {
	        return "//*[@" + "id='" + id + "']";
	    } else if (classAttr != null && !classAttr.isEmpty()) {
	        return "//" + tag + "[@class='" + classAttr + "']";
	    } else if (typeAttr != null && !typeAttr.isEmpty()) {
	        return "//" + tag + "[@type='" + typeAttr + "']";
	    } else {
	        return "//" + tag;
	    }
	}

	public static void saveElementToJsonUsingTAB(WebElement element, String xpath) {
	    try {
	        //String JSON_FILE_PATH = System.getProperty("user.dir") + "\\OR_Main.json";
	        File file = new File(JSON_FILE_PATH);

	        // Load existing data into LinkedHashMap
	        LinkedHashMap<String, JSONObject> jsonMap = new LinkedHashMap<>();
	        if (file.exists() && file.length() > 0) {
	            String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
	            JSONObject fullObject = new JSONObject(content);
	            for (String key : fullObject.keySet()) {
	                jsonMap.put(key, fullObject.getJSONObject(key));
	            }
	        }

	        String id = element.getAttribute("id");
	        String tag = element.getTagName();
	        String type = element.getAttribute("type");

	        // Determine key
	        String key = (id != null && !id.isEmpty()) ? id : tag + "_" + type;
	        key = key.replaceAll("[^a-zA-Z0-9_\\s]", "").trim();  // Clean unwanted characters

	        // Avoid duplication based on xpath
	        boolean exists = jsonMap.values().stream().anyMatch(obj -> xpath.equals(obj.optString("xpath")));
	        if (exists) {
	            System.out.println("Duplicate XPath detected. Not saving: " + xpath);
	            return;
	        }

	        JSONObject obj = new JSONObject();
	        obj.put("xpath", xpath);
	        obj.put("id", id != null ? id : "");
	        obj.put("tag", tag != null ? tag : "");
	        obj.put("type", type != null ? type : "");

	        jsonMap.put(key, obj);

	        // Save in insertion order
	        JSONObject finalJson = new JSONObject();
	        for (Map.Entry<String, JSONObject> entry : jsonMap.entrySet()) {
	            finalJson.put(entry.getKey(), entry.getValue());
	        }

	        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
	            writer.write(finalJson.toString(4));
	        }

	        System.out.println("Saved TAB XPath to OR_Main.json: " + xpath);

	    } catch (Exception e) {
	        System.err.println("Error saving TAB XPath to OR_Main.json: " + e.getMessage());
	    }
	}

	 */

	//NEW FUNCTIONS APPENDED - 05142025
	public static void saveElementToJsonUsingTAB(WebElement element, String xpath) {
		try {
			File file = new File(JSON_FILE_PATH);

			// Load existing data into LinkedHashMap
			LinkedHashMap<String, JSONObject> jsonMap = new LinkedHashMap<>();
			if (file.exists() && file.length() > 0) {
				String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
				JSONObject fullObject = new JSONObject(content);
				for (String key : fullObject.keySet()) {
					jsonMap.put(key, fullObject.getJSONObject(key));
				}
			}

			String id = element.getAttribute("id");
			String tag = element.getTagName();
			String type = element.getAttribute("type");

			// Clean field name key: prioritize ID, fallback to tag + type
			String rawKey = (id != null && !id.isEmpty()) ? id : tag + "_" + (type != null ? type : "");
			String key = rawKey.replaceAll("[^a-zA-Z0-9_\\s]", "").trim();

			// Avoid duplication based on XPath
			boolean exists = jsonMap.values().stream().anyMatch(obj -> xpath.equals(obj.optString("xpath")));
			if (exists) {
				System.out.println("Duplicate XPath detected. Not saving: " + xpath);
				return;
			}

			JSONObject obj = new JSONObject();
			obj.put("xpath", xpath);
			obj.put("id", id != null ? id : "");
			obj.put("tag", tag != null ? tag : "");
			obj.put("type", type != null ? type : "");
			obj = updateMetadata(key, obj, "Tab Click");
			jsonMap.put(key, obj);

			// Save in insertion order
			JSONObject finalJson = new JSONObject();
			for (Map.Entry<String, JSONObject> entry : jsonMap.entrySet()) {
				finalJson.put(entry.getKey(), entry.getValue());
			}

			try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
				writer.write(finalJson.toString(4));
			}
			sortJsonByClickCounterPreservingOthers();
			reorderPageNameToTop(JSON_FILE_PATH);
			System.out.println("Saved TAB XPath to OR_Main.json: " + xpath);

		} catch (Exception e) {
			System.err.println("Error saving TAB XPath to OR_Main.json: " + e.getMessage());
		}
	}

	public static String getXPathOfElementUsingTAB(WebDriver driver, WebElement element) {
		try {
			String tag = element.getTagName();
			String id = element.getDomAttribute("id");
			String classAttr = element.getDomAttribute("class");
			String typeAttr = element.getDomAttribute("type");

			// Use ID when available (clean format)
			if (id != null && !id.isEmpty()) {
				return "//*[@id='" + id + "']";
			}

			// For dropdowns
			if ("select".equalsIgnoreCase(tag)) {
				if (id != null && !id.isEmpty()) {
					return "//*[@id='" + id + "']";
				} else if (classAttr != null && !classAttr.isEmpty()) {
					return "//select[@class='" + classAttr + "']";
				} else {
					return "//select";
				}
			}

			// For checkboxes and radio buttons
			if ("input".equalsIgnoreCase(tag) && (typeAttr != null &&
					(typeAttr.equalsIgnoreCase("checkbox") || typeAttr.equalsIgnoreCase("radio")))) {
				if (id != null && !id.isEmpty()) {
					return "//*[@id='" + id + "']";
				} else if (classAttr != null && !classAttr.isEmpty()) {
					return "//input[@class='" + classAttr + "' and @type='" + typeAttr + "']";
				} else {
					return "//input[@type='" + typeAttr + "']";
				}
			}

			// For other input elements (text, email, etc.)
			if ("input".equalsIgnoreCase(tag) && typeAttr != null && !typeAttr.isEmpty()) {
				if (id != null && !id.isEmpty()) {
					return "//*[@id='" + id + "']";
				} else if (classAttr != null && !classAttr.isEmpty()) {
					return "//input[@class='" + classAttr + "' and @type='" + typeAttr + "']";
				} else {
					return "//input[@type='" + typeAttr + "']";
				}
			}

			// For textarea
			if ("textarea".equalsIgnoreCase(tag)) {
				if (id != null && !id.isEmpty()) {
					return "//*[@id='" + id + "']";
				} else if (classAttr != null && !classAttr.isEmpty()) {
					return "//textarea[@class='" + classAttr + "']";
				} else {
					return "//textarea";
				}
			}

			// Fallback
			if (classAttr != null && !classAttr.isEmpty()) {
				return "//" + tag + "[@class='" + classAttr + "']";
			} else {
				return "//" + tag;
			}

		} catch (Exception e) {
			return "UNKNOWN_XPATH";
		}
	}
	//ADDED NEW FUNCTIONS : 05/21/2025

	public static void captureXPathByFieldName(WebDriver driver, String fieldName) {
		List<WebElement> elements = driver.findElements(By.xpath("//*[(self::input or self::select or self::textarea or self::button or self::a)]"));

		for (WebElement el : elements) {
			try {
				String labelText = getLabelTextForElementByFieldName(driver, el).trim().replaceAll("[^\\p{L}\\p{Nd} ]", "");
				String id = el.getDomProperty("id");
				String name = el.getDomProperty("name");
				String placeholder = el.getDomProperty("placeholder");
				String tagName = el.getTagName();
				String type = el.getDomProperty("type");

				boolean match =
						(labelText.equalsIgnoreCase(fieldName)) ||
						(id != null && id.equalsIgnoreCase(fieldName)) ||
						(name != null && name.equalsIgnoreCase(fieldName)) ||
						(placeholder != null && placeholder.equalsIgnoreCase(fieldName)) ||
						(el.getText().trim().equalsIgnoreCase(fieldName));

				if (match) {
					String xpath = generatePreferredXPathByFieldName(el);
					System.out.println("Matched field: " + fieldName);
					System.out.println("XPath: " + xpath);

					Map<String, String> elementData = new LinkedHashMap<>();
					elementData.put("xpath", xpath);
					elementData.put("id", id != null ? id : "");
					elementData.put("tag", tagName != null ? tagName : "");
					elementData.put("type", type != null ? type : "");

					saveFieldToJsonByFieldName(fieldName, elementData,xpath); // 👈 save directly here



					return;
				}

			} catch (Exception e) {
				System.err.println("Error matching element: " + e.getMessage());
			}
		}

		System.out.println("No element matched with name: " + fieldName);
	}

	public static String generatePreferredXPathByFieldName1(WebElement element) {
		String id = element.getDomProperty("id");
		String name = element.getDomProperty("name");

		if (id != null && !id.isEmpty()) {
			return "//*[@id='" + id + "']";
		} else if (name != null && !name.isEmpty()) {
			return "//*[@name='" + name + "']";
		} else {
			return generateXPathUsingClick(element); // fallback method from your existing logic
		}
	}

	public static String getLabelTextForElementByFieldName(WebDriver driver, WebElement element) {
		try {
			String id = element.getDomProperty("id");
			if (id != null && !id.isEmpty()) {
				List<WebElement> labels = driver.findElements(By.xpath("//label[@for='" + id + "']"));
				if (!labels.isEmpty()) {
					return labels.get(0).getText();
				}
			}

			WebElement parent = element.findElement(By.xpath("ancestor::label"));
			if (parent != null) {
				return parent.getText();
			}

		} catch (Exception ignored) {
		}

		return "";
	}

	public static void saveFieldToJsonByFieldName1(String fieldName, Map<String, String> elementData, String xpath) {
		try {
			File file = new File(JSON_FILE_PATH);

			// Load existing data into LinkedHashMap
			LinkedHashMap<String, JSONObject> jsonMap = new LinkedHashMap<>();
			if (file.exists() && file.length() > 0) {
				String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
				JSONObject fullObject = new JSONObject(content);
				for (String key : fullObject.keySet()) {
					jsonMap.put(key, fullObject.getJSONObject(key));
				}
			}

			String id = elementData.getOrDefault("id", "");
			String tag = elementData.getOrDefault("tag", "");
			String type = elementData.getOrDefault("type", "");

			// Clean the field key
			String key = fieldName.replaceAll("[^a-zA-Z0-9_\\s]", "").trim();

			// Avoid duplication based on XPath
			boolean exists = jsonMap.values().stream().anyMatch(obj -> xpath.equals(obj.optString("xpath")));
			if (exists) {
				System.out.println("Duplicate XPath detected. Not saving: " + xpath);
				return;
			}

			JSONObject obj = new JSONObject();
			obj.put("xpath", xpath);
			obj.put("id", id);
			obj.put("tag", tag);
			obj.put("type", type);

			jsonMap.put(key, obj);

			// Save in insertion order
			JSONObject finalJson = new JSONObject();
			for (Map.Entry<String, JSONObject> entry : jsonMap.entrySet()) {
				finalJson.put(entry.getKey(), entry.getValue());
			}

			try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
				writer.write(finalJson.toString(4));
			}

			System.out.println("Saved field '" + key + "' with XPath to OR_Main.json");

		} catch (Exception e) {
			System.err.println("Error saving field '" + fieldName + "' to OR_Main.json: " + e.getMessage());
		}
	}

	//05292025
//	public static String generatePreferredXPathByFieldName(WebElement element) {
//	    String name = element.getDomProperty("name");
//	    String id = element.getDomProperty("id");
//	    String tag = element.getTagName();
//
//	    if (name != null && !name.isEmpty() && id != null && !id.isEmpty()) {
//	        return "//" + tag + "[@name=\"" + name + "\" or @id='" + id + "']";
//	    } else if (name != null && !name.isEmpty()) {
//	        return "//" + tag + "[@name=\"" + name + "\"]";
//	    } else if (id != null && !id.isEmpty()) {
//	        return "//" + tag + "[@id='" + id + "']";
//	    } else {
//	        return generateXPathUsingClick(element); // fallback logic
//	    }
//	}
	
	public static String generatePreferredXPathByFieldName(WebElement element) {
	    String tag = element.getTagName();
	    String name = element.getDomProperty("name");
	    String id = element.getDomProperty("id");
	    String text = element.getText().trim();

	    // Prefer name and id together
	    if (name != null && !name.isEmpty() && id != null && !id.isEmpty()) {
	        return "//" + tag + "[@name=\"" + name + "\" or @id='" + id + "']";
	    }

	    // Prefer name
	    if (name != null && !name.isEmpty()) {
	        return "//" + tag + "[@name=\"" + name + "\"]";
	    }

	    // Prefer id
	    if (id != null && !id.isEmpty()) {
	        return "//" + tag + "[@id='" + id + "']";
	    }

	    // If it's a button or link and has visible text, use that
	    if ((tag.equalsIgnoreCase("button") || tag.equalsIgnoreCase("a")) && !text.isEmpty()) {
	        return "//" + tag + "[normalize-space(text())=\"" + text + "\"]";
	    }

	    // Fallback
	    return generateXPathUsingFieldName(element);
	}

	public static void saveFieldToJsonByFieldName(String fieldName, Map<String, String> elementData, String xpath) {
		try {
			File file = new File(JSON_FILE_PATH);

			// Load existing data
			LinkedHashMap<String, JSONObject> jsonMap = new LinkedHashMap<>();
			if (file.exists() && file.length() > 0) {
				String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
				JSONObject fullObject = new JSONObject(content);
				for (String key : fullObject.keySet()) {
					jsonMap.put(key, fullObject.getJSONObject(key));
				}
			}

			String id = elementData.getOrDefault("id", "");
			String tag = elementData.getOrDefault("tag", "");
			String type = elementData.getOrDefault("type", "");

			String key = fieldName.replaceAll("[^a-zA-Z0-9_\\s]", "").trim();

			// Avoid duplicate XPath entries
			boolean exists = jsonMap.values().stream().anyMatch(obj -> xpath.equals(obj.optString("xpath")));
			if (exists) {
				System.out.println("Duplicate XPath detected. Not saving: " + xpath);
				return;
			}

			JSONObject obj = new JSONObject();
			obj.put("xpath", xpath);  // xpath with name attribute format (e.g., //input[@name="email"])
			obj.put("id", id);
			obj.put("tag", tag);
			obj.put("type", type);
			obj = updateMetadata(fieldName, obj, "Object Name");
			jsonMap.put(key, obj);

			// Write back to file in order
			JSONObject finalJson = new JSONObject();
			for (Map.Entry<String, JSONObject> entry : jsonMap.entrySet()) {
				finalJson.put(entry.getKey(), entry.getValue());
			}

			try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
				writer.write(finalJson.toString(4));
			}
			sortJsonByClickCounterPreservingOthers();
			reorderPageNameToTop(JSON_FILE_PATH);
			System.out.println("Saved field '" + key + "' with XPath to OR_Main.json");

		} catch (Exception e) {
			System.err.println("Error saving field '" + fieldName + "' to OR_Main.json: " + e.getMessage());
		}
	}
	
	//06032025 - CHETANM
	public static String generateXPathUsingClick(WebElement element) {
		String tag = element.getTagName();
		String id = element.getDomAttribute("id");
		String classAttr = element.getDomAttribute("class");
		String typeAttr = element.getDomAttribute("type");
	
		if (id != null && !id.isEmpty()) {
			return "//*[@" + "id='" + id + "']";
		} else if (classAttr != null && !classAttr.isEmpty()) {
			return "//" + tag + "[@class='" + classAttr + "']";
		} else if (typeAttr != null && !typeAttr.isEmpty()) {
			return "//" + tag + "[@type='" + typeAttr + "']";
		} else {
			return "//" + tag;
		}
	}

	
	public static String generateXPathUsingFieldName(WebElement element) {
	    String tag = element.getTagName();
	    String id = element.getDomAttribute("id");
	    String name = element.getDomAttribute("name");
	    String type = element.getDomAttribute("type");
	    String text = element.getText().trim();

	    // Prefer both name and id if available
	    if (name != null && !name.isEmpty() && id != null && !id.isEmpty()) {
	        return "//" + tag + "[@name=\"" + name + "\" or @id='" + id + "']";
	    }

	    // Prefer name
	    if (name != null && !name.isEmpty()) {
	        return "//" + tag + "[@name=\"" + name + "\"]";
	    }

	    // Then id
	    if (id != null && !id.isEmpty()) {
	        return "//" + tag + "[@id='" + id + "']";
	    }

	    // For buttons or anchors with visible text
	    if ((tag.equalsIgnoreCase("button") || tag.equalsIgnoreCase("a")) && !text.isEmpty()) {
	        return "//" + tag + "[normalize-space(text())=\"" + text + "\"]";
	    }

	    // Use type only if above are missing
	    if (type != null && !type.isEmpty()) {
	        return "//" + tag + "[@type='" + type + "']";
	    }

	    // Fallback: only tag
	    return "//" + tag;
	}
	
	public static List<WebElement> getAllPopUpDropdownContainers_DynamicXpathGenerator1() throws InterruptedException, IOException {
		try {
			//m_Operation.waitForPageToCompletelyLoad();
			WebDriverWait wait  = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
			wait
			.pollingEvery(Duration.ofMillis(500))
			.ignoring(Throwable.class)
			.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
					By.xpath("//div[@id='modal-body'] //input/following-sibling::label/ancestor::div[contains(@class,'row')]")));
			List<WebElement> dropdownContainers = getDriver().findElements(By.xpath("//div[@id='modal-body'] //input/following-sibling::label/ancestor::div[contains(@class,'row')]"));

			int i = 0;
			// Print all labels first
			System.out.println("Iteration "+i + ":\t");
			dropdownContainers.stream()
			.map(s -> s.findElement(By.tagName("label")).getText())
			.forEach(s -> System.out.print(s + ", "));

			// Check if any label is empty (case-insensitive)
			boolean hasEmptyLabel = dropdownContainers.stream()
					.map(s -> s.findElement(By.tagName("label")).getText())
					.anyMatch(s -> s.equalsIgnoreCase(""));

			ObjectMapper mapper = new ObjectMapper();
			File jsonFile = new File(JSON_FILE_PATH);

			// Step 1: Load existing JSON or create new one if file doesn't exist
			ObjectNode rootNode;
			if (jsonFile.exists()) {
				rootNode = (ObjectNode) mapper.readTree(jsonFile);
			} else {
				rootNode = JsonNodeFactory.instance.objectNode();
			}

			// Step 2: Append new entries
			for (WebElement ele : dropdownContainers) {
				String elementName = ele.findElement(By.tagName("label")).getText().trim();
				String xpath = "//div[@id='modal-body'] //label[contains(normalize-space(),'" + elementName + "')]/ancestor::div[contains(@class,'row')] //input";
				xpath = xpath.replace("\nDropdown", "");
				elementName = elementName.split("\n")[0];
				ObjectNode elementNode = JsonNodeFactory.instance.objectNode();
				elementNode.put("xpath", xpath);
				JSONObject newFieldData = convertObjectNodeToJSONObject(elementNode);
				newFieldData = updateMetadata(elementName, newFieldData, "Dropdown");
				elementNode = convertJSONObjectToObjectNode(newFieldData);
				// Add or overwrite the key
				rootNode.set(elementName, elementNode);
			}
			String elementOption = "DropdownOptionName";
			String xpath = "//li/div[@role='option']";
			// Check if the key already exists in the rootNode
			if (!rootNode.has(elementOption)) {
			    ObjectNode elementNode = JsonNodeFactory.instance.objectNode();
			    elementNode.put("xpath", xpath);
			    JSONObject newFieldData = convertObjectNodeToJSONObject(elementNode);
				newFieldData = updateMetadata(elementOption, newFieldData, "Dropdown");
				elementNode = convertJSONObjectToObjectNode(newFieldData);
			    rootNode.set(elementOption, elementNode);
			} 
			else 
			{
			    System.out.println("Key '" + elementOption + "' already exists. Skipping creation.");
			}
			// Step 3: Write updated content back to file
			mapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, rootNode);
			sortJsonByClickCounterPreservingOthers();
			reorderPageNameToTop(JSON_FILE_PATH);
			System.out.println("Appended JSON written successfully to: " + JSON_FILE_PATH);
			Thread.sleep(500);


			Thread.sleep(500);

			System.out.println("Popup Dropdowns are found.");
			return dropdownContainers;

		}catch(Exception e)
		{
			System.out.println("Popup Dropdown not found");
		}
		return null;
	}
	public static ObjectNode convertJSONObjectToObjectNode(JSONObject jsonObject) {
	    ObjectMapper mapper = new ObjectMapper();
	    return mapper.convertValue(jsonObject.toMap(), ObjectNode.class);
	}
	

	public static List<WebElement> getAllDynamicDropdownContainers_DynamicXpathGenerator() throws InterruptedException, StreamWriteException, DatabindException, IOException {
		try {
			//m_Operation.waitForPageToCompletelyLoad();
			WebDriverWait wait  = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
			wait
			.pollingEvery(Duration.ofMillis(500))
			.ignoring(Throwable.class)
			.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
					By.xpath("//input[@role='combobox']/ancestor::div[contains(@class,'wrap') or contains(@class,'required')]")
					));
			List<WebElement> dropdownContainers = getDriver().findElements(By.xpath("//input[@role='combobox']/ancestor::div[contains(@class,'wrap') or contains(@class,'required')]"));

			int i = 0;
			// Print all labels first
			System.out.println("Iteration "+i + ":\t");
			dropdownContainers.stream()
			.map(s -> s.findElement(By.tagName("label")).getText())
			.forEach(s -> System.out.print(s + ", "));

			// Check if any label is empty (case-insensitive)
			boolean hasEmptyLabel = dropdownContainers.stream()
					.map(s -> s.findElement(By.tagName("label")).getText())
					.anyMatch(s -> s.equalsIgnoreCase(""));

			ObjectMapper mapper = new ObjectMapper();
			File jsonFile = new File(JSON_FILE_PATH);

			// Step 1: Load existing JSON or create new one if file doesn't exist
			ObjectNode rootNode;
			if (jsonFile.exists()) {
				rootNode = (ObjectNode) mapper.readTree(jsonFile);
			} else {
				rootNode = JsonNodeFactory.instance.objectNode();
			}

			// Step 2: Append new entries
			for (WebElement ele : dropdownContainers) {
				String elementName = ele.findElement(By.tagName("label")).getText().trim();
				String xpath = "//label[contains(normalize-space(),'" + elementName + "')]/ancestor::div[contains(@class,'wrap') or contains(@class,'required')] //input[@role='combobox']";
				xpath = xpath.replace("\nDropdown", "");
				elementName = elementName.replace("\nDropdown", "");
				ObjectNode elementNode = JsonNodeFactory.instance.objectNode();
				elementNode.put("xpath", xpath);
				JSONObject newFieldData = convertObjectNodeToJSONObject(elementNode);
				newFieldData = updateMetadata(elementName, newFieldData, "Dropdown");
				elementNode = convertJSONObjectToObjectNode(newFieldData);
				rootNode.set(elementName, elementNode);
			}
			String elementOption = "DropdownOptionName";
			String xpath = "//li/div[@role='option']";
			// Check if the key already exists in the rootNode
			if (!rootNode.has(elementOption)) {
			    ObjectNode elementNode = JsonNodeFactory.instance.objectNode();
			    elementNode.put("xpath", xpath);
			    JSONObject newFieldData = convertObjectNodeToJSONObject(elementNode);
				newFieldData = updateMetadata(elementOption, newFieldData, "Dropdown");
				elementNode = convertJSONObjectToObjectNode(newFieldData);
				mapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, rootNode);
				sortJsonByClickCounterPreservingOthers();
				reorderPageNameToTop(JSON_FILE_PATH);
			    rootNode.set(elementOption, elementNode);
			} 
			else 
			{
			    System.out.println("Key '" + elementOption + "' already exists. Skipping creation.");
			}
			System.out.println("Appended JSON written successfully to: " + JSON_FILE_PATH);
			Thread.sleep(500);

			Thread.sleep(500);

			System.out.println("Dynamic Dropdowns are found.");
			return dropdownContainers;
		}catch(Exception e)
		{
			System.out.println("Dynamic Dropdown not found");
		}
		return null;
	}
	
	public static JSONObject convertObjectNodeToJSONObject(ObjectNode objectNode) {
	    ObjectMapper mapper = new ObjectMapper();
	    return new JSONObject(mapper.convertValue(objectNode, java.util.Map.class));
	}
	
	
	public static JSONObject updateMetadata(String fieldName, JSONObject newFieldData,String foundBy) 
	{
	    counter++;
	    newFieldData.put("counter", String.valueOf(counter));
	    newFieldData.put("Found By", foundBy);
	    //newFieldData.put("_comment", "This is a comment for developers");
	    String currentUrl = m_driver.getCurrentUrl();
	    String pageName = derivePageName(currentUrl, m_driver.getTitle());
	    newFieldData.put("page_name", pageName);
	    return newFieldData;
	}
	
	public static int findMaxCounterValue(String jsonFilePath) {
	    ObjectMapper mapper = new ObjectMapper();
	    int maxCounter = 0;

	    try {
	        File file = new File(jsonFilePath);

	        // File doesn't exist or is empty
	        if (!file.exists() || file.length() == 0) {
	            return 0;
	        }

	        JsonNode rootNode = mapper.readTree(file);

	        if (rootNode == null || !rootNode.isObject()) {
	            return 0;
	        }

	        Iterator<Map.Entry<String, JsonNode>> fields = rootNode.fields();
	        while (fields.hasNext()) {
	            Map.Entry<String, JsonNode> entry = fields.next();

	            // Skip the datetimestamp node
	            if ("datetimestamp".equalsIgnoreCase(entry.getKey())) {
	                continue;
	            }

	            JsonNode valueNode = entry.getValue();
	            if (valueNode.has("counter")) {
	                JsonNode counterNode = valueNode.get("counter");

	                if (counterNode != null) {
	                    if (counterNode.isInt()) {
	                        maxCounter = Math.max(maxCounter, counterNode.intValue());
	                    } else if (counterNode.isTextual()) {
	                        try {
	                            int parsed = Integer.parseInt(counterNode.asText().trim());
	                            maxCounter = Math.max(maxCounter, parsed);
	                        } catch (NumberFormatException e) {
	                            // Ignore non-numeric strings
	                        }
	                    }
	                }
	            }
	        }

	    } catch (Exception e) {
	        // Log error but continue returning 0
	        System.err.println("Error reading or parsing JSON for max counter: " + e.getMessage());
	    }

	    return maxCounter;
	}
	
	public static String getCurrentTimestamp() {
		return new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(new Date());
	}
	

	public static void saveOrUpdateDateTimestamp3(boolean isStart) 
	{
	    try {
	    	counter++;
	    	datetimestampCounter++;
	        File file = new File(JSON_FILE_PATH);
	        JSONObject originalRoot = new JSONObject();

	        // Step 1: Read existing JSON
	        if (file.exists() && file.length() > 0) {
	            try {
	                String content = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH)));
	                originalRoot = new JSONObject(content);
	            } catch (Exception e) {
	                System.err.println("Warning: JSON file exists but unreadable. Starting fresh.");
	            }
	        }

	        // Step 2: Create or update datetimestamp block
	        JSONObject timestampBlock = originalRoot.has("datetimestamp"+datetimestampCounter)
	                ? originalRoot.getJSONObject("datetimestamp"+datetimestampCounter) 
	                : new JSONObject();

	        if (isStart) {
	            timestampBlock.put("CaptureStartTime", getCurrentTimestamp());
	            timestampBlock.put("counter", Integer.toString(counter));
	        } else {
	            timestampBlock.put("CaptureEndTime", getCurrentTimestamp());
	        }

	        // Step 3: Rebuild JSON with datetimestamp as the first key
	        JSONObject reorderedRoot = new JSONObject();
	        reorderedRoot.put("datetimestamp"+datetimestampCounter, timestampBlock);  // force top insertion

	        // Copy all other entries (except old datetimestamp)
	        for (String key : originalRoot.keySet()) {
	            if (!key.equals("datetimestamp"+datetimestampCounter)) {
	                reorderedRoot.put(key, originalRoot.get(key));
	            }
	        }

	        // Step 4: Write back to file
	        try (FileWriter writer = new FileWriter(file)) {
	            writer.write(reorderedRoot.toString(4)); // pretty-print
	        }

	    } catch (Exception e) {
	        System.err.println("Error saving/updating datetimestamp: " + e.getMessage());
	    }
	}
	

//	public static void sortJsonByClickCounterPreservingOthers() 
//	{
//			try 
//			{
//				File file = new File(JSON_FILE_PATH);
//				ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
//
//				// Load existing JSON
//				LinkedHashMap<String, LinkedHashMap<String, String>> originalMap = new LinkedHashMap<>();
//				if (file.exists() && file.length() > 0) {
//					originalMap = mapper.readValue(file,
//							new TypeReference<LinkedHashMap<String, LinkedHashMap<String, String>>>() {});
//				}
//
//				String sortCounter = "counter";
//
//				// Separate entries
//				List<Map.Entry<String, LinkedHashMap<String, String>>> validEntries = new ArrayList<>();
//				List<Map.Entry<String, LinkedHashMap<String, String>>> invalidEntries = new ArrayList<>();
//				LinkedHashMap<String, LinkedHashMap<String, String>> preservedEntries = new LinkedHashMap<>();
//
//				for (Map.Entry<String, LinkedHashMap<String, String>> entry : originalMap.entrySet()) {
//					String key = entry.getKey();
//
//					// Skip datetimestamp node
//					if ("datetimestamp".equalsIgnoreCase(key)) {
//						preservedEntries.put(key, entry.getValue());
//						continue;
//					}
//
//					String val = entry.getValue().get(sortCounter);
//					if (val != null) {
//						try {
//							Integer.parseInt(val.trim());
//							validEntries.add(entry);
//						} catch (NumberFormatException e) {
//							invalidEntries.add(entry);
//						}
//					} else {
//						invalidEntries.add(entry);
//					}
//				}
//
//				// Sort valid entries by counter
//				validEntries.sort(Comparator.comparingInt(e -> Integer.parseInt(e.getValue().get(sortCounter).trim())));
//
//				// Reconstruct final map
//				LinkedHashMap<String, LinkedHashMap<String, String>> sortedMap = new LinkedHashMap<>();
//
//				// Preserve 'datetimestamp' node at the top
//				sortedMap.putAll(preservedEntries);
//
//				// Reassign new counter values to sorted valid entries
//				int newCounter = 1;
//				for (Map.Entry<String, LinkedHashMap<String, String>> entry : validEntries) {
//					entry.getValue().put(sortCounter, String.valueOf(newCounter++));
//					sortedMap.put(entry.getKey(), entry.getValue());
//				}
//
//				// Add remaining (invalid/missing counter) entries
//				for (Map.Entry<String, LinkedHashMap<String, String>> entry : invalidEntries) {
//					entry.getValue().put(sortCounter, String.valueOf(newCounter++));
//					sortedMap.put(entry.getKey(), entry.getValue());
//				}
//
//				// Write back to file
//				mapper.writeValue(file, sortedMap);
//				System.out.println("JSON sorted and re-sequenced by '" + sortCounter + "' with 'datetimestamp' preserved.");
//
//			} 
//			catch (Exception e) 
//			{
//				System.err.println("Error during sorting and reassigning counters: " + e.getMessage());
//			}
//		}
	//NEW 06122025

	public static void sortJsonByClickCounterPreservingOthers() {
	    try {
	        File file = new File(JSON_FILE_PATH);
	        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

	        // Load existing JSON
	        LinkedHashMap<String, LinkedHashMap<String, String>> originalMap = new LinkedHashMap<>();
	        if (file.exists() && file.length() > 0) {
	            originalMap = mapper.readValue(file,
	                    new TypeReference<LinkedHashMap<String, LinkedHashMap<String, String>>>() {});
	        }

	        // Sort entries that contain a numeric "counter" field
	        List<Map.Entry<String, LinkedHashMap<String, String>>> sortedEntries = originalMap.entrySet()
	            .stream()
	            .sorted(Comparator.comparingInt(entry -> {
	                String counterStr = entry.getValue().get("counter");
	                try {
	                    return counterStr != null ? Integer.parseInt(counterStr.trim()) : Integer.MAX_VALUE;
	                } catch (NumberFormatException e) {
	                    return Integer.MAX_VALUE; // push non-numeric counters to end
	                }
	            }))
	            .collect(Collectors.toList());

	        // Reconstruct sorted map
	        LinkedHashMap<String, LinkedHashMap<String, String>> sortedMap = new LinkedHashMap<>();
	        for (Map.Entry<String, LinkedHashMap<String, String>> entry : sortedEntries) {
	            sortedMap.put(entry.getKey(), entry.getValue());
	        }

	        // Write back to file
	        mapper.writeValue(file, sortedMap);
	        System.out.println("JSON sorted strictly in ascending order by 'counter'.");

	    } catch (Exception e) {
	        System.err.println("Error during sorting by counter: " + e.getMessage());
	    }
	}


	public static int findMaxCounterValuedate(String jsonFilePath) {
	    ObjectMapper mapper = new ObjectMapper();
	    int maxCounter = 0;

	    try {
	        File file = new File(jsonFilePath);

	        // File doesn't exist or is empty
	        if (!file.exists() || file.length() == 0) {
	            return 0;
	        }

	        JsonNode rootNode = mapper.readTree(file);

	        if (rootNode == null || !rootNode.isObject()) {
	            return 0;
	        }

	        Iterator<String> fieldNames = rootNode.fieldNames();
	        while (fieldNames.hasNext()) {
	            String key = fieldNames.next();

	            if (key.toLowerCase().startsWith("datetimestamp")) {
	                String numberPart = key.replaceAll("[^0-9]", "");
	                if (!numberPart.isEmpty()) {
	                    try {
	                        int num = Integer.parseInt(numberPart);
	                        maxCounter = Math.max(maxCounter, num);
	                    } catch (NumberFormatException e) {
	                        // Skip invalid number suffix
	                    }
	                }
	            }
	        }

	    } catch (Exception e) {
	    	return 0;
	        //System.err.println("Error reading or parsing JSON for datetimestamp counter: " + e.getMessage());
	    }

	    return maxCounter;
	}
	
	public static void quitDriver(WebDriver driver) {
	    try {
	        if (driver != null) {
	            driver.quit(); // Closes all browser windows and ends the WebDriver session
	            System.out.println("---------- Browser closed Successfully ----------");
	        }
	    } catch (Exception e) {
	        System.err.println("Error while quitting the driver: " + e.getMessage());
	    }
	}
	
	
	
	public static void captureFullPageScreenshots2(WebDriver driver) throws IOException {
	    driver.manage().window().maximize(); // Ensure full width is visible

	    String screenshotFolder = System.getProperty("user.dir") + "\\resources\\screenshots";
	    File folder = new File(screenshotFolder);
	    if (!folder.exists()) folder.mkdirs();

	    int screenshotIndex = getNextScreenshotIndex(folder);

	    Screenshot screenshot = new AShot()
	        .shootingStrategy(ShootingStrategies.viewportPasting(
	            ShootingStrategies.scaling(1.25f), // Capture full width
	            1000 // Scroll delay
	        ))
	        .takeScreenshot(driver);

	    String filePath = screenshotFolder + "\\fullpage_screenshot " + screenshotIndex + ".png";
	    ImageIO.write(screenshot.getImage(), "PNG", new File(filePath));
	    System.out.println("Full-page screenshot saved to: " + filePath);
	}

	private static int getNextScreenshotIndex(File folder) {
	    int maxIndex = 0;
	    Pattern pattern = Pattern.compile("fullpage_screenshot (\\d+)\\.png");

	    for (File file : folder.listFiles()) {
	        Matcher matcher = pattern.matcher(file.getName());
	        if (matcher.matches()) {
	            try {
	                int index = Integer.parseInt(matcher.group(1));
	                if (index > maxIndex) {
	                    maxIndex = index;
	                }
	            } catch (NumberFormatException ignored) {
	            }
	        }
	    }

	    return maxIndex + 1;
	}
	
	//------------------------------ 09/19/2025 -----------------------------------// 
	public static List<WebElement> getAllPopUpDropdownContainers_DynamicXpathGenerator() throws InterruptedException, IOException {
	    try {
	        // Wait for page to load and dropdowns to be visible
	        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
	        
	        // First, try to find dropdowns in modal-body (original logic)
	        List<WebElement> dropdownContainers = new ArrayList<>();
	        try {
	            wait.pollingEvery(Duration.ofMillis(500))
	                .ignoring(Throwable.class)
	                .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
	                    By.xpath("//div[@id='modal-body'] //input/following-sibling::label/ancestor::div[contains(@class,'row')]")));
	            dropdownContainers = getDriver().findElements(By.xpath("//div[@id='modal-body'] //input/following-sibling::label/ancestor::div[contains(@class,'row')]"));
	        } catch (Exception e) {
	            System.out.println("Modal-body dropdowns not found, trying alternative approach...");
	        }
	        
	        // Second, try to find ol-dropdown elements (new logic for your use case)
	        List<WebElement> olDropdownContainers = new ArrayList<>();
	        try {
	            olDropdownContainers = getDriver().findElements(By.xpath("//ol-dropdown | //ol-form-group[.//ol-dropdown]"));
	            System.out.println("Found " + olDropdownContainers.size() + " ol-dropdown containers");
	        } catch (Exception e) {
	            System.out.println("ol-dropdown elements not found");
	        }
	        
	        // Combine both types of dropdowns
	        List<WebElement> allDropdowns = new ArrayList<>();
	        allDropdowns.addAll(dropdownContainers);
	        allDropdowns.addAll(olDropdownContainers);
	        
	        if (allDropdowns.isEmpty()) {
	            System.out.println("No dropdown containers found");
	            return null;
	        }
	        
	        ObjectMapper mapper = new ObjectMapper();
	        File jsonFile = new File(JSON_FILE_PATH);
	        
	        // Load existing JSON or create new one
	        ObjectNode rootNode;
	        if (jsonFile.exists()) {
	            rootNode = (ObjectNode) mapper.readTree(jsonFile);
	        } else {
	            rootNode = JsonNodeFactory.instance.objectNode();
	        }
	        
	        int dropdownCounter = 0;
	        
	        // Process original modal-body dropdowns
	        for (WebElement ele : dropdownContainers) {
	            try {
	                String elementName = ele.findElement(By.tagName("label")).getText().trim();
	                String xpath = "//div[@id='modal-body'] //label[contains(normalize-space(),'" + elementName + "')]/ancestor::div[contains(@class,'row')] //input";
	                xpath = xpath.replace("\nDropdown", "");
	                elementName = elementName.split("\n")[0];
	                
	                ObjectNode elementNode = JsonNodeFactory.instance.objectNode();
	                elementNode.put("xpath", xpath);
	                JSONObject newFieldData = convertObjectNodeToJSONObject(elementNode);
	                newFieldData = updateMetadata(elementName, newFieldData, "Dropdown");
	                elementNode = convertJSONObjectToObjectNode(newFieldData);
	                
	                rootNode.set(elementName, elementNode);
	                dropdownCounter++;
	                System.out.println("Captured modal dropdown: " + elementName);
	            } catch (Exception e) {
	                System.err.println("Error processing modal dropdown: " + e.getMessage());
	            }
	        }
	        
	        // Process ol-dropdown elements (new logic)
	        for (WebElement olDropdown : olDropdownContainers) {
	            try {
	                String elementName = "";
	                String outerXpath = "";
	                
	                // Try to find label for ol-dropdown
	                try {
	                    WebElement label = olDropdown.findElement(By.xpath(".//label | .//preceding-sibling::label | .//ancestor::*//label[following::*[.//ol-dropdown]]"));
	                    elementName = label.getText().trim();
	                    if (!elementName.isEmpty()) {
	                        // Generate XPath using label text
	                        outerXpath = "//label[normalize-space()='" + elementName + "']//ancestor::ol-form-group//ol-dropdown//div//div[@role='combobox']";
	                    }
	                } catch (Exception e) {
	                    // If no label found, try to use ID or generate generic xpath
	                    try {
	                        String dropdownId = olDropdown.getAttribute("id");
	                        if (dropdownId != null && !dropdownId.isEmpty()) {
	                            elementName = dropdownId + "_Dropdown";
	                            outerXpath = "//ol-dropdown[@id='" + dropdownId + "']//div[@role='combobox']";
	                        } else {
	                            elementName = "UnknownDropdown_" + dropdownCounter;
	                            outerXpath = "(//ol-dropdown//div[@role='combobox'])[" + (dropdownCounter + 1) + "]";
	                        }
	                    } catch (Exception ex) {
	                        elementName = "UnknownDropdown_" + dropdownCounter;
	                        outerXpath = "(//ol-dropdown//div[@role='combobox'])[" + (dropdownCounter + 1) + "]";
	                    }
	                }
	                
	                if (!elementName.isEmpty()) {
	                    // Clean up element name
	                    elementName = elementName.split("\n")[0].trim();
	                    if (elementName.isEmpty()) {
	                        elementName = "Dropdown_" + dropdownCounter;
	                    }
	                    
	                    // Create outer dropdown entry
	                    ObjectNode outerElementNode = JsonNodeFactory.instance.objectNode();
	                    outerElementNode.put("xpath", outerXpath);
	                    JSONObject outerFieldData = convertObjectNodeToJSONObject(outerElementNode);
	                    outerFieldData = updateMetadata(elementName, outerFieldData, "Dropdown");
	                    outerElementNode = convertJSONObjectToObjectNode(outerFieldData);
	                    rootNode.set(elementName, outerElementNode);
	                    
	                    System.out.println("Captured ol-dropdown outer: " + elementName + " -> " + outerXpath);
	                    
	                    // Create inner options entry
	                    String optionsElementName = elementName + "_Options";
	                    String optionsXpath = "//ol-option[@role='option']//span[contains(@class,'ol-ellipsis')]";
	                    
	                    // Check if options key already exists
	                    if (!rootNode.has(optionsElementName)) {
	                        ObjectNode optionsElementNode = JsonNodeFactory.instance.objectNode();
	                        optionsElementNode.put("xpath", optionsXpath);
	                        JSONObject optionsFieldData = convertObjectNodeToJSONObject(optionsElementNode);
	                        optionsFieldData = updateMetadata(optionsElementName, optionsFieldData, "DropdownOption");
	                        optionsElementNode = convertJSONObjectToObjectNode(optionsFieldData);
	                        rootNode.set(optionsElementName, optionsElementNode);
	                        
	                        System.out.println("Captured ol-dropdown options: " + optionsElementName + " -> " + optionsXpath);
	                    }
	                    
	                    dropdownCounter++;
	                }
	            } catch (Exception e) {
	                System.err.println("Error processing ol-dropdown: " + e.getMessage());
	            }
	        }
	        
	        // Add generic dropdown option xpath if not already present
	        String genericOptionElement = "DropdownOptionName";
	        String genericOptionXpath = "//li/div[@role='option'] | //ol-option[@role='option']//span[contains(@class,'ol-ellipsis')]";
	        
	        if (!rootNode.has(genericOptionElement)) {
	            ObjectNode elementNode = JsonNodeFactory.instance.objectNode();
	            elementNode.put("xpath", genericOptionXpath);
	            JSONObject newFieldData = convertObjectNodeToJSONObject(elementNode);
	            newFieldData = updateMetadata(genericOptionElement, newFieldData, "DropdownOption");
	            elementNode = convertJSONObjectToObjectNode(newFieldData);
	            rootNode.set(genericOptionElement, elementNode);
	            System.out.println("Added generic dropdown options xpath");
	        }
	        
	        // Write updated content back to file
	        mapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, rootNode);
	        sortJsonByClickCounterPreservingOthers();
	        reorderPageNameToTop(JSON_FILE_PATH);
	        
	        System.out.println("Updated JSON written successfully to: " + JSON_FILE_PATH);
	        System.out.println("Total dropdowns captured: " + dropdownCounter);
	        
	        Thread.sleep(500);
	        System.out.println("Popup Dropdowns capture completed.");
	        
	        return allDropdowns;
	        
	    } catch (Exception e) {
	        System.err.println("Error in dropdown capture: " + e.getMessage());
	        e.printStackTrace();
	        System.out.println("Popup Dropdown capture failed");
	    }
	    return null;
	}


} //MAIN BRACKET
