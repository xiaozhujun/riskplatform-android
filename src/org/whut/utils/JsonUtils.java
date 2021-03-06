package org.whut.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.whut.database.entity.Device;


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
			map.put("equipmentVariety",(i+1)+"、"+jsonItem.getString("equipmentVariety"));
			map.put("riskValue", "风险值："+jsonItem.getString("riskValue"));
			map.put("userPoint","使用地点："+jsonItem.getString("userPoint"));
			data.add(map);			
		}
		//		//测试数据
		//		for(int j=0;j<7;j++){
		//			Map<String,String> map = new HashMap<String,String>();
		//			map.put("unitAddress", "测试数据一");
		//			map.put("equipmentVariety",(j+2)+"、门式起重机");
		//			map.put("riskValue", "风险值：6");
		//			map.put("userPoint","使用地点：厂房");
		//			data.add(map);
		//		}
		return data;
	}

	public static List<Map<String,String>> getProvinceRisk(String message) throws Exception{
		List<Map<String,String>> data=new ArrayList<Map<String,String>>();
		JSONObject jsonObject = new JSONObject(message);
		JSONArray jsonArray = jsonObject.getJSONArray("data");
		for(int i=0;i<jsonArray.length();i++){
			Map<String,String> map = new HashMap<String,String>();
			JSONObject jsonItem = jsonArray.getJSONObject(i);
			String province = jsonItem.getString("province");
			double avgRiskValue = jsonItem.getDouble("avgRiskValue"); 
			Integer craneNumber = jsonItem.getInt("craneNumber");
			map.put("province", province);
			map.put("province_rank", "第 "+(i+1)+" 名："+province);
			map.put("avgRiskValue", "平均风险值："+avgRiskValue);
			map.put("craneNumber", "设备数量："+craneNumber);
			data.add(map);
		}
		return data;		
	}




	public static List<Map<String,String>> getCityRisk(String message) throws Exception{
		List<Map<String,String>> data=new ArrayList<Map<String,String>>();
		JSONObject jsonObject = new JSONObject(message);
		JSONArray jsonArray = jsonObject.getJSONArray("data");
		for(int i=0;i<jsonArray.length();i++){
			Map<String,String> map = new HashMap<String,String>();
			JSONObject jsonItem = jsonArray.getJSONObject(i);
			String city = jsonItem.getString("city");
			double avgRiskValue = jsonItem.getDouble("avgRiskValue"); 
			Integer craneNumber = jsonItem.getInt("craneNumber");
			map.put("city", city);
			map.put("city_rank", "第 "+(i+1)+" 名："+city);
			map.put("avgRiskValue", "平均风险值："+avgRiskValue);
			map.put("craneNumber", "设备数量："+craneNumber);
			data.add(map);
		}
		return data;		
	}



	public static List<Map<String,String>> getAreaRisk(String message) throws Exception{

		List<Map<String,String>> data=new ArrayList<Map<String,String>>();
		JSONObject jsonObject = new JSONObject(message);
		JSONArray jsonArray = jsonObject.getJSONArray("data");
		for(int i=0;i<jsonArray.length();i++){
			Map<String,String> map = new HashMap<String,String>();
			JSONObject jsonItem = jsonArray.getJSONObject(i);
			String area = jsonItem.getString("area");
			double avgRiskValue = jsonItem.getDouble("avgRiskValue"); 
			Integer craneNumber = jsonItem.getInt("craneNumber");
			map.put("area", area);
			map.put("area_rank", "第 "+(i+1)+" 名："+area);
			map.put("avgRiskValue", "平均风险值："+avgRiskValue);
			map.put("craneNumber", "设备数量："+craneNumber);
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
			map.put("id", "风险排名 : 第 "+j+" 名");
			map.put("riskValue","风险值:"+avgRiskValue);
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
			String result = jsonItem.getString("lng")+","+jsonItem.getString("lat")+","+(jsonItem.getInt("riskValue")+","+jsonItem.getString("craneNumber")+","+jsonItem.getString("safeManager")+","+jsonItem.getString("contactPhone"));
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

	public static List<String> getRiskValues(String message) throws Exception{
		// TODO Auto-generated method stub
		return null;
	}

	public static List<String> getSafeManagers(String message) throws Exception{
		// TODO Auto-generated method stub
		return null;
	}

	public static int[] GetRankStatistics(String message) throws Exception{

		JSONObject jsonObject = new JSONObject(message);
		JSONArray jsonArray = jsonObject.getJSONArray("data");
		int rankArray[] = new int[jsonArray.length()];
		//初始化rankArray
		for(int i=0;i<rankArray.length;i++){
			rankArray[i] = 0;
		}
		for(int i=0;i<jsonArray.length();i++){
			JSONObject jsonItem =jsonArray.getJSONObject(i);
			rankArray[jsonItem.getInt("riskValue")]+=jsonItem.getInt("craneNumber");
		}
		return rankArray;
	}

	public static List<Device> getEquipInfoDataForDeviceId(String message) throws Exception{
		// TODO Auto-generated method stub
		List<Device> data=new ArrayList<Device>();
		JSONObject jsonObject = new JSONObject(message);
		JSONArray jsonArray = jsonObject.getJSONArray("data");
		for(int i=0;i<jsonArray.length();i++){
			JSONObject jsonItem = jsonArray.getJSONObject(i);
			Device device  =  new Device(jsonItem.getString("unitAddress"),jsonItem.getString("equipmentVariety"),jsonItem.getInt("riskValue"),jsonItem.getString("userPoint"));
			data.add(device);			
		}	
		return data;
	}

	public static List<Map<String, String>> getResultList(String message) throws Exception{
		// TODO Auto-generated method stub
		List<Map<String,String>> data=new ArrayList<Map<String,String>>();
		JSONObject jsonObject  = new JSONObject(message);
		JSONArray jsonArray = jsonObject.getJSONArray("data");
		for(int i=0;i<jsonArray.length();i++){
			Map<String,String> map = new HashMap<String, String>();
			JSONObject jsonItem = jsonArray.getJSONObject(i);
			map.put("equipmentVariety",(i+1)+"."+jsonItem.getString("equipmentVariety"));
			map.put("riskValue", "风险值："+jsonItem.getInt("riskValue")+"");
			map.put("useTime", "使用年限："+jsonItem.getInt("useTime")+"");
			map.put("unitAddress", "公司地址："+jsonItem.getString("unitAddress"));
			data.add(map);
		}
		return data;
	}

	public static List<Map<String, String>> GetDistributeData(String message) throws Exception{
		// TODO Auto-generated method stub
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		int[] level = new int[10];
		int sum = 0;
		for(int i=0;i<level.length;i++){
			level[i] = 0;
		}
		JSONObject jsonObject = new JSONObject(message);
		JSONArray jsonArray = jsonObject.getJSONArray("data");
		for(int i=0;i<jsonArray.length();i++){
			JSONObject jsonItem = jsonArray.getJSONObject(i);
			sum = sum + jsonItem.getInt("craneNumber");
			int riskValue = jsonItem.getInt("riskValue");
			if(riskValue>=0&&riskValue<=10){
				level[0] = level[0]+jsonItem.getInt("craneNumber");
			}else if(riskValue>10&&riskValue<=20){
				level[1] = level[1]+jsonItem.getInt("craneNumber");
			}else if(riskValue>20&&riskValue<=30){
				level[2] = level[2]+jsonItem.getInt("craneNumber");
			}else if(riskValue>30&&riskValue<=40){
				level[3] = level[3]+jsonItem.getInt("craneNumber");
			}else if(riskValue>40&&riskValue<=50){
				level[4] = level[4]+jsonItem.getInt("craneNumber");
			}else if(riskValue>50&&riskValue<=60){
				level[5] = level[5]+jsonItem.getInt("craneNumber");
			}else if(riskValue>60&&riskValue<=70){
				level[6] = level[6]+jsonItem.getInt("craneNumber");
			}else if(riskValue>70&&riskValue<=80){
				level[7] = level[7]+jsonItem.getInt("craneNumber");
			}else if(riskValue>80&&riskValue<=90){
				level[8] = level[8]+jsonItem.getInt("craneNumber");
			}else if(riskValue>90&&riskValue<=100){
				level[9] = level[9]+jsonItem.getInt("craneNumber");
			}
		}
		for(int i=0;i<level.length;i++){
			Map<String,String> map = new HashMap<String, String>();
			map.put("riskLevel", i+1+"");
			map.put("craneNumbers", level[i]+"");
			double temp = ((double)level[i]/sum)*100;
			int temp1 = (int)Math.round(temp*100);
			double result = (double)temp1/100.00;
			map.put("scale", result+"%");
			list.add(map);
		}
		return list;
	}


}
