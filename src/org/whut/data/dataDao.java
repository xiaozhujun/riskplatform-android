package org.whut.data;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class dataDao {
	private MySQLite mySQLite;
	private SQLiteDatabase db;

	public dataDao(Context context) {
		mySQLite = new MySQLite(context);
	}

	public void insert(CollectionData collectionData) {
		db = mySQLite.getWritableDatabase();
		db.execSQL(
				"insert into data (unitAddress,equipmentVariety,riskValue,userPoint) values(?,?,?,?) ",
				new Object[] { collectionData.getunitAddress(),
						collectionData.getequipmentVariety(),
						collectionData.getriskValue(),
						collectionData.getuserPoint() });
	}

	public CollectionData query(String unitAddress) {
		db = mySQLite.getWritableDatabase();
		Cursor cursor = db
				.rawQuery(
						"select  unitAddress,equipmentVariety,riskValue,userPoint from data where unitAddress=?",
						new String[]{unitAddress});
		if (cursor.moveToNext()) {
			return new CollectionData(cursor.getString(cursor
					.getColumnIndex("unitAddress")), cursor.getString(cursor
					.getColumnIndex("equipmentVariety")),
					cursor.getString(cursor.getColumnIndex("riskValue")),
					cursor.getString(cursor.getColumnIndex("userPoint")));
		}
		return null;
	}

	public void delete(String unitAddress) {
		db = mySQLite.getWritableDatabase();

		db.execSQL("delete from data where unitAddress=?",new String[]{ unitAddress});

	}
	public void update(CollectionData collectionData)
	{
		db=mySQLite.getWritableDatabase();
		db.execSQL("update data set unitAddress=?,equipmentVariety=?,riskValue=?,userPoint=?", new Object[]{collectionData.getunitAddress(),collectionData.getequipmentVariety(),collectionData.getriskValue(),collectionData.getuserPoint()});
		
	}

	public List<CollectionData> get() {
		List<CollectionData> cd = new ArrayList<CollectionData>();
		db = mySQLite.getWritableDatabase();
		Cursor cursor = db.rawQuery("select unitAddress,equipmentVariety,riskValue,userPoint from data ",  null);
		while (cursor.moveToNext()) {
			cd.add(new CollectionData(cursor.getString(cursor.getColumnIndex("unitAddress")), cursor.getString(cursor.getColumnIndex("equipmentVariety")), cursor.getString(cursor.getColumnIndex("riskValue")), cursor.getString(cursor.getColumnIndex("userPoint"))));
		}
		return cd;
	}
  public void Delete(String... unitAddress){
	  if (unitAddress.length > 0) {  
          StringBuffer sb = new StringBuffer(); 
          String[] aa=new String[unitAddress.length];
          for (int i=0;i<aa.length;i++) {  
              sb.append('?').append(','); 
              aa[i] = unitAddress[i];
          }  
          sb.deleteCharAt(sb.length() - 1);  
          db = mySQLite.getWritableDatabase();  
          db.execSQL(  
                  "delete from data where unitAddress in(" + sb.toString()  
                          + ")", unitAddress);  
      }  
  }
	

}
