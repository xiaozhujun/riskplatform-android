package org.whut.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
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
		int j=1;
		List<Map<String,String>> data=new ArrayList<Map<String,String>>();
		JSONObject jsonObject = new JSONObject(message);
		JSONArray jsonArray = jsonObject.getJSONArray("data");
		int[] temp=new int[jsonArray.length()];
		for(int i=0;i<jsonArray.length();i++){
			Map<String,String> map = new HashMap<String, String>(); 
			JSONObject jsonItem =jsonArray.getJSONObject(i);
			int avgRiskValue=jsonItem.getInt("avgRiskValue");
			String province=jsonItem.getString("province");
			temp[i]=avgRiskValue;
			if (i>0) {
				if (!(temp[i]==temp[i-1])) {
					j++;
				}
				}
			
			map.put("id", j+"");
			map.put("avgRiskValue",avgRiskValue+"");
			map.put("province",province);
			data.add(map);
		}
		return data;		
	}
	
	
	

	public static List<Map<String,String>> getCityRisk(String message) throws Exception{
		int j=1;
		List<Map<String,String>> data=new ArrayList<Map<String,String>>();
		JSONObject jsonObject = new JSONObject(message);
		JSONArray jsonArray = jsonObject.getJSONArray("data");
		int[] temp=new int[jsonArray.length()];
		for(int i=0;i<jsonArray.length();i++){
			Map<String,String> map = new HashMap<String, String>(); 
			JSONObject jsonItem =jsonArray.getJSONObject(i);
			int avgRiskValue=jsonItem.getInt("avgRiskValue");
			String city=jsonItem.getString("city");
			temp[i]=avgRiskValue;
			if (i>0) {
				if (!(temp[i]==temp[i-1])) {
					j++;
				}
			}
			
			map.put("id", j+"");
			map.put("avgRiskValue",avgRiskValue+"");
			map.put("city",city);
			data.add(map);
		}
		return data;		
	}
	
	

	public static List<Map<String,String>> getAreaRisk(String message) throws Exception{
		int j=1;
		List<Map<String,String>> data=new ArrayList<Map<String,String>>();
		JSONObject jsonObject = new JSONObject(message);
		JSONArray jsonArray = jsonObject.getJSONArray("data");
		int[] temp=new int[jsonArray.length()];
		for(int i=0;i<jsonArray.length();i++){
			Map<String,String> map = new HashMap<String, String>(); 
			JSONObject jsonItem =jsonArray.getJSONObject(i);
			String area=jsonItem.getString("area");
			int avgRiskValue=jsonItem.getInt("avgRiskValue");
			temp[i]=avgRiskValue;
			if (i>0) {
				if (!(temp[i]==temp[i-1])) {
					j++;
				}
			}
			
			map.put("id", j+"");
			map.put("avgRiskValue",avgRiskValue+"");
			map.put("area",area);
			data.add(map);
		}
		return data;		
	}

	

	public static List<Map<String, String>> getCompanyRankList(String message) throws Exception{
		int j=1;
		List<Map<String,String>> data=new ArrayList<Map<String,String>>();
		JSONObject jsonObject = new JSONObject(message);
		JSONArray jsonArray = jsonObject.getJSONArray("data");
		int[] temp=new int[jsonArray.length()];
		for(int i=0;i<jsonArray.length();i++){
			Map<String,String> map = new HashMap<String,String>();
			JSONObject jsonItem = jsonArray.getJSONObject(i);
			int avgRiskValue=jsonItem.getInt("riskValue");
			temp[i]=avgRiskValue;
			if(i>0){
			if (!(temp[i]==temp[i-1])) {
				j++;
			}
			}
			map.put("id", j+"");
			map.put("riskValue",avgRiskValue+"");
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
	
	
	//2014/5/18新增加接口 得到设备类型
	public static List<String> getEquipmentVarietyList(String message) throws Exception{
		List<String> list = new ArrayList<String>();
		list.add("设备类型");
		JSONObject jsonObject = new JSONObject(message);
		JSONArray jsonArray = jsonObject.getJSONArray("data");
		for(int i=0;i<jsonArray.length();i++){
			JSONObject jsonItem = jsonArray.getJSONObject(i);
			String unitAddress = jsonItem.getString("equipmentVariety");
			list.add(unitAddress);
		}
		return list;	
	}
	//2014/5/18新增加接口数据处理：省搜索结果数据
	public static List<Map<String,String>> getProvinceInfoWithDataRule(String message) throws Exception{
		List<Map<String,String>> data=new ArrayList<Map<String,String>>();
		JSONObject jsonObject = new JSONObject(message);
		JSONArray jsonArray = jsonObject.getJSONArray("data");
		for(int i=0;i<jsonArray.length();i++){
			Map<String,String> map = new HashMap<String, String>(); 
			JSONObject jsonItem =jsonArray.getJSONObject(i);
			String craneNumber=jsonItem.getString("craneNumber");
			String province=jsonItem.getString("province");
			map.put("riskValue",jsonItem.getString("riskvalue"));
			map.put("craneNumber",craneNumber);
			map.put("province",province);
			data.add(map);
		}
		//风险值排序
		Collections.sort(data,new Comparator<Map<String, String>>() {

		    @Override
		    public int compare(Map<String, String> o1, Map<String, String> o2) {
		        String name1 = o1.get("riskValue");
		        String name2 = o2.get("riskValue");
		        if (name1 == null) return -1;
		        return name1.compareTo(name2);
		    }
		});
		return data;		
	}
	//2014/5/18新增加接口数据处理：城市搜索结果数据
	public static List<Map<String,String>> getCityInfoByCondition(String message) throws Exception{
		List<Map<String,String>> data=new ArrayList<Map<String,String>>();
		JSONObject jsonObject = new JSONObject(message);
		JSONArray jsonArray = jsonObject.getJSONArray("data");
		for(int i=0;i<jsonArray.length();i++){
			Map<String,String> map = new HashMap<String, String>(); 
			JSONObject jsonItem =jsonArray.getJSONObject(i);
			String craneNumber=jsonItem.getString("craneNumber");
			String city=jsonItem.getString("city");
			map.put("riskValue",jsonItem.getString("riskvalue"));
			map.put("craneNumber",craneNumber);
			map.put("city",city);
			data.add(map);
		}
		//风险值排序
		Collections.sort(data,new Comparator<Map<String, String>>() {

		    @Override
		    public int compare(Map<String, String> o1, Map<String, String> o2) {
		        String name1 = o1.get("riskValue");
		        String name2 = o2.get("riskValue");
		        if (name1 == null) return -1;
		        return name1.compareTo(name2);
		    }
		});
		return data;		
	}
	//2014/5/18新增加接口数据处理：区域搜索结果数据
	public static List<Map<String,String>> getAreaInfoByCondition(String message) throws Exception{
		List<Map<String,String>> data=new ArrayList<Map<String,String>>();
		JSONObject jsonObject = new JSONObject(message);
		JSONArray jsonArray = jsonObject.getJSONArray("data");
		for(int i=0;i<jsonArray.length();i++){
			Map<String,String> map = new HashMap<String, String>(); 
			JSONObject jsonItem =jsonArray.getJSONObject(i);
			String craneNumber=jsonItem.getString("craneNumber");
			String area=jsonItem.getString("area");
			map.put("riskValue",jsonItem.getString("riskvalue"));
			map.put("craneNumber",craneNumber);
			map.put("area",area);
			data.add(map);
		}
		//风险值排序
		Collections.sort(data,new Comparator<Map<String, String>>() {

		    @Override
		    public int compare(Map<String, String> o1, Map<String, String> o2) {
		        String name1 = o1.get("riskValue");
		        String name2 = o2.get("riskValue");
		        if (name1 == null) return -1;
		        return name1.compareTo(name2);
		    }
		});
		return data;		
	}
	
	//2014/5/18新增加接口数据处理：省市区搜索结果数据
		public static List<Map<String,String>> getCraneInfoByCondition(String message) throws Exception{
			List<Map<String,String>> data=new ArrayList<Map<String,String>>();
			JSONObject jsonObject = new JSONObject(message);
			JSONArray jsonArray = jsonObject.getJSONArray("data");
			for(int i=0;i<jsonArray.length();i++){
				Map<String,String> map = new HashMap<String, String>(); 
				JSONObject jsonItem =jsonArray.getJSONObject(i);
				String craneNumber=jsonItem.getString("craneNumber");
				map.put("riskValue",jsonItem.getString("riskvalue"));
				map.put("craneNumber",craneNumber);
				map.put("company_name",jsonItem.getString("unitAddress"));
				data.add(map);
			}
			//风险值排序
			Collections.sort(data,new Comparator<Map<String, String>>() {

			    @Override
			    public int compare(Map<String, String> o1, Map<String, String> o2) {
			        String name1 = o1.get("riskValue");
			        String name2 = o2.get("riskValue");
			        if (name1 == null) return -1;
			        return name1.compareTo(name2);
			    }
			});
			return data;		
		}
	
	
}
