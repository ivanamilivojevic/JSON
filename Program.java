package domaci;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Program {
	static long lastRequestTimestamp;
	static enum Type { CURRENT, FORECAST };
	public static String getWeatherInfo(String city, String countryCode, Type type) throws IOException {
		String authToken = "d0f1969fd9856fe09e3f7d0753d84ed4";
		String addr = String.format("http://api.openweathermap.org/data/2.5/%s?q=%s,%s&appid=%s&units=metric&lang=sr", type == Type.CURRENT ? "weather" : "forecast", city, countryCode,authToken );
		
		if(System.currentTimeMillis() - lastRequestTimestamp < 5000) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) { 
				e.printStackTrace(); 
			}
		}
			
		URLConnection yc = new URL(addr).openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
		
		StringBuilder buffer = new StringBuilder();
		String inputLine;
		while ((inputLine = in.readLine()) != null) 
			buffer.append(inputLine);
		in.close();
		lastRequestTimestamp = System.currentTimeMillis();
		return buffer.toString();
	}
		
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Dobrodošli u konzolnu vremensku prognozu!");
		System.out.println("-----------------------------------------");
		String komande = "Komande:\n" + 
				"-1) Izlaz iz programa\n" + 
				" 0) Promena grada [String]\n" + 
				" 1) Trenutna temperatura\n" + 
				" 2) Prognoza za 5 dana\n" + 
				"99) Ispis komandnog menija";
		
		String[] location = {"Belgrade","RS"};
		JSONObject json, temperatures;
		try {
			for(int komanda=99;komanda!=-1;komanda=Integer.parseInt(sc.nextLine())) {
				switch(komanda) {
				case 0:
					System.out.println("Unesite grad u formatu: city, country_code");
					location = sc.nextLine().split(",");
					break;
				case 1:
					json = (JSONObject) new JSONParser().parse(getWeatherInfo(location[0], location[1], Type.CURRENT));
					temperatures = (JSONObject) json.get("main");
					System.out.printf("Trenutna:\t%s°C\n",temperatures.get("temp"));
					System.out.printf("Maksimalna:\t%s°C\n",temperatures.get("temp_max"));
					System.out.printf("Minimalna:\t%s°C\n",temperatures.get("temp_min"));
					break;
				case 2:
					json = (JSONObject) new JSONParser().parse(getWeatherInfo(location[0],  location[1], Type.FORECAST));
					JSONArray list = (JSONArray) json.get("list");
					
					Calendar calendar = Calendar.getInstance();
					String[] dani = {"Ned","Pon","Uto","Sre","Èet","Pet","Sub"};
					
					for(int i=0;i<list.size();i+=8) {
						calendar.add(Calendar.DATE, 1);
						int index = calendar.get(Calendar.DAY_OF_WEEK)-1;
						json = (JSONObject) list.get(i);
						temperatures = (JSONObject) json.get("main");
						
						System.out.printf("%s:\t%s°C\n",dani[index],temperatures.get("temp_max"));
					}
					break;
				case 99:
					System.out.println(komande);
					break;
				default:
					System.err.println("Nepostojeca komanda! pokusajte ponovo");
				}
				
				System.out.println("----------------------------");
				System.out.print  ("komanda: ");
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
