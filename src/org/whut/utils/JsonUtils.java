package org.whut.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class JsonUtils {


	public static String getLatLng(String message) throws Exception{
		JSONObject jsonObject = new JSONObject(message);
		return jsonObject.getString("str");		
	}

	public static List<Map<String, String>> getRankList(String message) throws Exception{
		// TODO Auto-generated method stub
		List<Map<String,String>> data=new ArrayList<Map<String,String>>();
		JSONObject jsonObject = new JSONObject(message);
		JSONArray jsonArray = jsonObject.getJSONArray("data");
		for(int i=0;i<jsonArray.length();i++){
			Map<String,String> map = new HashMap<String,String>();
			JSONObject jsonItem = jsonArray.getJSONObject(i);
			map.put("riskValue",jsonItem.getInt("riskValue")+"");
			//暂时先用unitAddress代替company_name,之后会对数据库进行调整
			map.put("company_name",jsonItem.getString("unitAddress"));
			map.put("types",jsonItem.getString("equipmentVariety"));
			data.add(map);
		}

		return data;
	}

	public static List<Map<String,String>> getInfoData(String message) throws Exception{
		// TODO Auto-generated method stub
		List<Map<String,String>> data=new ArrayList<Map<String,String>>();
		JSONObject jsonObject = new JSONObject(message);
		JSONArray jsonArray = jsonObject.getJSONArray("data");
		for(int i=0;i<jsonArray.length();i++){
			Map<String,String> map = new HashMap<String,String>();
			JSONObject jsonItem = jsonArray.getJSONObject(i);
			map.put("unitAddress",jsonItem.getString("unitAddress"));
			map.put("addressId", jsonItem.getString("addressId"));
			map.put("organizeCode",jsonItem.getString("organizeCode"));
			map.put("userPoint",jsonItem.getString("userPoint"));
			map.put("safeManager",jsonItem.getString("safeManager"));
			map.put("contactPhone",jsonItem.getString("contactPhone"));
			map.put("equipmentVariety",jsonItem.getString("equipmentVariety"));
			map.put("unitNumber",jsonItem.getString("unitNumber"));
			map.put("manufactureUnit", jsonItem.getString("manufactureUnit"));
			map.put("manufactureDate", jsonItem.getString("manufactureDate"));
			map.put("specification", jsonItem.getString("specification"));
			map.put("workLevel",jsonItem.getString("workLevel"));
			map.put("riskValue", jsonItem.getString("riskValue"));
			map.put("ratedLiftWeight", jsonItem.getString("ratedLiftWeight"));
			data.add(map);
		}
		return data;
	}

	public static List<String> initProvince(String message) throws Exception{
		// TODO Auto-generated method stub
		List<String> list = new ArrayList<String>();
		list.add("请选择省份");
		JSONObject jsonObject = new JSONObject(message);
		JSONArray jsonArray = jsonObject.getJSONArray("data");
		for(int i=0;i<jsonArray.length();i++){
			JSONObject jsonItem = jsonArray.getJSONObject(i);
			list.add(jsonItem.getString("province"));			
		}		
		return list;
	}

	public static List<String> initCity(String message) throws Exception{
		List<String> list = new ArrayList<String>();
		list.add("请选择城市");
		JSONObject jsonObject = new JSONObject(message);
		JSONArray jsonArray = jsonObject.getJSONArray("data");
		for(int i=0;i<jsonArray.length();i++){
			JSONObject jsonItem = jsonArray.getJSONObject(i);
			list.add(jsonItem.getString("city"));
		}		
		return list;
	}

	public static List<String> initArea(String message) throws Exception{
		List<String> list = new ArrayList<String>();
		list.add("请选择地区");
		JSONObject jsonObject = new JSONObject(message);
		JSONArray jsonArray = jsonObject.getJSONArray("data");
		for(int i=0;i<jsonArray.length();i++){
			JSONObject jsonItem = jsonArray.getJSONObject(i);
			list.add(jsonItem.getString("area"));
		}
		return list;
	}

	public static List<Map<String, String>> getEquipInfoData(String message) throws Exception{
		// TODO Auto-generated method stub
		List<Map<String,String>> data=new ArrayList<Map<String,String>>();
		JSONObject jsonObject = new JSONObject(message);
		JSONArray jsonArray = jsonObject.getJSONArray("data");
		for(int i=0;i<jsonArray.length();i++){
			Map<String,String> map = new HashMap<String,String>();
			JSONObject jsonItem = jsonArray.getJSONObject(i);				
			map.put("unitAddress", jsonItem.getString("unitAddress"));
			map.put("equipmentVariety",jsonItem.getString("equipmentVariety"));
			map.put("riskValue", jsonItem.getString("riskValue"));
			map.put("userPoint",jsonItem.getString("userPoint"));
			data.add(map);			
		}
		return data;
	}

	public static List<Map<String,String>> getProvinceRisk(String message) throws Exception{
		List<Map<String,String>> data=new ArrayList<Map<String,String>>();
		JSONObject jsonObject = new JSONObject(message);
		JSONArray jsonArray = jsonObject.getJSONArray("data");
		for(int i=0;i<jsonArray.length();i++){
			Map<String,String> map = new HashMap<String, String>(); 
			JSONObject jsonItem =jsonArray.getJSONObject(i);
			String avgRiskValue=jsonItem.getString("avgRiskValue");
			String province=jsonItem.getString("province");
			map.put("id", String.valueOf(i+1));
			map.put("avgRiskValue",avgRiskValue);
			map.put("province",province);
			data.add(map);
		}
		return data;		
	}

	public static List<Map<String,String>> getCityRisk(String message) throws Exception{
		List<Map<String,String>> data=new ArrayList<Map<String,String>>();
		JSONObject jsonObject = new JSONObject(message);
		JSONArray jsonArray = jsonObject.getJSONArray("data");
		for(int i=0;i<jsonArray.length();i++){
			Map<String,String> map = new HashMap<String, String>(); 
			JSONObject jsonItem =jsonArray.getJSONObject(i);
			String avgRiskValue=jsonItem.getString("avgRiskValue");
			String city=jsonItem.getString("city");
			map.put("id", String.valueOf(i+1));
			map.put("avgRiskValue",avgRiskValue);
			map.put("city",city);
			data.add(map);
		}
		return data;		
	}

	public static List<Map<String,String>> getAreaRisk(String message) throws Exception{
		List<Map<String,String>> data=new ArrayList<Map<String,String>>();
		JSONObject jsonObject = new JSONObject(message);
		JSONArray jsonArray = jsonObject.getJSONArray("data");
		for(int i=0;i<jsonArray.length();i++){
			Map<String,String> map = new HashMap<String, String>(); 
			JSONObject jsonItem =jsonArray.getJSONObject(i);
			String avgRiskValue=jsonItem.getString("avgRiskValue");
			String area=jsonItem.getString("area");
			map.put("id", String.valueOf(i+1));
			map.put("avgRiskValue",avgRiskValue);
			map.put("area",area);
			data.add(map);
		}
		return data;		
	}


	public static List<Map<String, String>> getCompanyRankList(String message) throws Exception{
		// TODO Auto-generated method stub
		List<Map<String,String>> data=new ArrayList<Map<String,String>>();
		JSONObject jsonObject = new JSONObject(message);
		JSONArray jsonArray = jsonObject.getJSONArray("data");
		for(int i=0;i<jsonArray.length();i++){
			Map<String,String> map = new HashMap<String,String>();
			JSONObject jsonItem = jsonArray.getJSONObject(i);
			map.put("id", String.valueOf(i+1));
			map.put("riskValue",jsonItem.getString("riskValue"));
			map.put("company_name",jsonItem.getString("unitAddress"));
			data.add(map);
		}

		return data;
	}

	public static List<String> getCompanyPosition(String message) throws Exception{
		List<String> list = new ArrayList<String>();
		JSONObject jsonObject = new JSONObject(message);
		JSONArray jsonArray = jsonObject.getJSONArray("data");
		for(int i=0;i<jsonArray.length();i++){
			JSONObject jsonItem = jsonArray.getJSONObject(i);
			String result = jsonItem.getString("lng")+","+jsonItem.getString("lat")+","+(jsonItem.getInt("riskValue")+"");
			list.add(result);
		}				
		return list;	
	}

	public static List<String> getUnitAddress(String message) throws Exception{
		List<String> list = new ArrayList<String>();
		JSONObject jsonObject = new JSONObject(message);
		JSONArray jsonArray = jsonObject.getJSONArray("data");
		for(int i=0;i<jsonArray.length();i++){
			JSONObject jsonItem = jsonArray.getJSONObject(i);
			String unitAddress = jsonItem.getString("unitAddress");
			list.add(unitAddress);
		}
		return list;	
	}
}
