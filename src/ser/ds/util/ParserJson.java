package ser.ds.util;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import com.google.gson.Gson;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

public class ParserJson
{
	String Url = "";
	private WebServiceSoap mWebSoap;

	public ParserJson(String pUrl, Context pContext)
	{
		Url = Util.getServerAddress(pContext) + pUrl;
		mWebSoap = new WebServiceSoap(pContext);
	}

	public ParserJson(Context pContext)
	{
		mWebSoap = new WebServiceSoap(pContext);
	}

	//
	public List findforlist(Class pClazz, String Method, Map<String, Object> value, String jsonflag) throws Exception
	{
		List<Object> Fieldlist = new ArrayList<Object>();
		Object obj = null;
		// ҵ��������������
		Field[] fields = null;
		String result = null;
		try
		{
			//
			Gson gson = new Gson();
			String jsonTxt = gson.toJson(value);
			if (jsonflag.equals("openfire"))// 
			{
				result = NetTool.sendTxt(Url, jsonTxt, "UTF-8");
			}
			else if (jsonflag.equals("SoilWaterHttpService"))
			{
				String url = "http://10.16.148.52:8090/wegisdbaccess/DBAcess/WaterSystem?type=1425282731559";
				result = HttpServiceSoap.readParse(url);//
			}
			if (result.equals(""))
				return null;

			JSONObject jsonObj = new JSONObject(result);//
			if (pClazz == null)// 
			{
				if (jsonObj.has("message"))
				{
					Util.CurrentMessage = jsonObj.getString("message");
				}
				
				return null;
			} else
			{
				fields = pClazz.getDeclaredFields();
				if (jsonObj.has("message"))
				{
					Util.CurrentMessage = jsonObj.getString("message");
				}
				if (jsonObj.has("functionList"))// 本用户的权限列表
				{
					JSONArray jsA = jsonObj.getJSONArray("functionList");
					for (int i = 0; i < jsA.length(); i++)
					{
						//Util.authorities = ParamUtil.authorities + jsA.get(i).toString() + "/";
					}
				}
				if (jsonObj.has("nodeList"))
				{
					JSONArray jsA = jsonObj.getJSONArray("nodeList");
					for (int i = 0; i < jsA.length(); i++)
					{
						JSONObject Obj = jsA.getJSONObject(i);
						obj = pClazz.newInstance();
						for (int j = 0; j < fields.length; j++)
						{
							Field f = fields[j];
							boolean flag = f.isAccessible();
							f.setAccessible(true);
							try
							{
								// 
								if (Obj.has(f.getName().toString()) && Obj.getString(f.getName().toString()) != null)
								{
									if (Obj.getString(f.getName().toString()).toString().equals("anyType{}"))// 
										f.set(obj, null);//
									else
										f.set(obj, Obj.getString(f.getName()).toString());// 
								}
							} catch (Exception e)
							{
								throw e;
							}
							f.setAccessible(flag);
						}
						Fieldlist.add(obj);
					}
				} else if (jsonObj.has("userInfo"))
				{
					String user = jsonObj.getString("userInfo");
					JSONObject Obj = new JSONObject(user);
					// 
					for (int i = 0; i < 1; i++)
					{
						obj = pClazz.newInstance();
						for (int j = 0; j < fields.length; j++)
						{
							Field f = fields[j];
							boolean flag = f.isAccessible();
							f.setAccessible(true);
							try
							{
								// Ԫ�ش���
								if (Obj.has(f.getName().toString()) && Obj.getString(f.getName().toString()) != null)
								{
									if (Obj.getString(f.getName().toString()).toString().equals("anyType{}"))// ֵ
										f.set(obj, null);// ֵ
									else
										f.set(obj, Obj.getString(f.getName()).toString());//
								}
							} catch (Exception e)
							{
								throw e;
							}
							f.setAccessible(flag);
						}
						Fieldlist.add(obj);
					}
				} else if (jsonObj.has("Data") && jsonflag.equals("RadioDetect"))
				{
					JSONArray jsA = jsonObj.getJSONArray("Data");
					for (int i = 0; i < 7; i++)
					{
						JSONObject Obj = jsA.getJSONObject(i);
						obj = pClazz.newInstance();
						for (int j = 0; j < fields.length; j++)
						{
							Field f = fields[j];
							boolean flag = f.isAccessible();
							f.setAccessible(true);
							try
							{
								// 
								if (Obj.has(f.getName().toString()) && Obj.getString(f.getName().toString()) != null)
								{
									if (Obj.getString(f.getName().toString()).toString().equals("anyType{}"))// ֵ
										f.set(obj, null);//ֵ
									else
										f.set(obj, Obj.getString(f.getName()).toString());// 
								}
							} catch (Exception e)
							{
								throw e;
							}
							f.setAccessible(flag);
						}
						Fieldlist.add(obj);
					}
				} else if (jsonObj.has("Data") && jsonflag.equals("Satellite"))
				{
					JSONArray jsA = jsonObj.getJSONArray("Data");
					for (int i = 0; i < 7; i++)
					{
						JSONObject Obj = jsA.getJSONObject(i);
						obj = pClazz.newInstance();
						for (int j = 0; j < fields.length; j++)
						{
							Field f = fields[j];
							boolean flag = f.isAccessible();
							f.setAccessible(true);
							try
							{
								// Ԫ�ش���
								if (Obj.has(f.getName().toString()) && Obj.getString(f.getName().toString()) != null)
								{
									if (Obj.getString(f.getName().toString()).toString().equals("anyType{}"))// ֵΪ��
										f.set(obj, null);// ����ֵ�������ֵ����᷵��anyType{}��Ϊ��Ԫ�ر�ǩ��ֵ
									else
										f.set(obj, Obj.getString(f.getName()).toString());// f�Ĵ�Сдһ��Ҫ��xml��ǩ��һ��
								}
							} catch (Exception e)
							{
								throw e;
							}
							f.setAccessible(flag);
						}
						Fieldlist.add(obj);
					}
				} else if (jsonObj.has("Data") && jsonflag.equals("Weather"))
				{
					JSONObject js = null;
					JSONArray jsA = jsonObj.getJSONArray("Data");
					for (int i = 0; i < jsA.length(); i++)
					{ 

						if (jsA.getJSONObject(i).getString("City").equals("西安"))
						{
							js = jsA.getJSONObject(i);
						}

					}
					// JSONObject js = jsA.getJSONObject(6);
					JSONArray jsnow = js.getJSONArray("Forecast");
					for (int i = 0; i < 7; i++)
					{
						JSONObject Obj = jsnow.getJSONObject(i);
						obj = pClazz.newInstance();
						for (int j = 0; j < fields.length; j++)
						{
							Field f = fields[j];
							boolean flag = f.isAccessible();
							f.setAccessible(true);
							try
							{
								// Ԫ�ش���
								if (Obj.has(f.getName().toString()) && Obj.getString(f.getName().toString()) != null)
								{
									if (Obj.getString(f.getName().toString()).toString().equals("anyType{}"))// ֵΪ��
										f.set(obj, null);// ����ֵ�������ֵ����᷵��anyType{}��Ϊ��Ԫ�ر�ǩ��ֵ
									else
										f.set(obj, Obj.getString(f.getName()).toString());// f�Ĵ�Сдһ��Ҫ��xml��ǩ��һ��
								}
							} catch (Exception e)
							{
								throw e;
							}
							f.setAccessible(flag);
						}
						Fieldlist.add(obj);
					}
				} else if (jsonObj.has("infoList"))
				{
					JSONArray jsA = jsonObj.getJSONArray("infoList");
					for (int i = 0; i < jsA.length(); i++)// 行政区划
					{
						JSONObject Objm = jsA.getJSONObject(i);
						List fielditem = new ArrayList();
						if (Objm.has("skinfo"))
						{
							JSONArray jsB = Objm.getJSONArray("skinfo");
							for (int x = 0; x < jsB.length(); x++)// 水库
							{
								JSONObject Obj = jsB.getJSONObject(x);

								obj = pClazz.newInstance();
								for (int j = 0; j < fields.length; j++)
								{
									Field f = fields[j];
									boolean flag = f.isAccessible();
									f.setAccessible(true);
									try
									{
										// Ԫ�ش���
										if (Obj.has(f.getName().toString()) && Obj.getString(f.getName().toString()) != null)
										{
											if (Obj.getString(f.getName().toString()).toString().equals("anyType{}"))// ֵΪ��
												f.set(obj, null);// ����ֵ�������ֵ����᷵��anyType{}��Ϊ��Ԫ�ر�ǩ��ֵ
											else
												f.set(obj, Obj.getString(f.getName()).toString());// f�Ĵ�Сдһ��Ҫ��xml��ǩ��һ��
										}
									} catch (Exception e)
									{
										throw e;
									}
									f.setAccessible(flag);
								}
								fielditem.add(obj);// �?��行政区划的水库加入到�?��列表�?
							}
							Fieldlist.add(fielditem);// �?��行政区划的数�?
						}
					}
				}
				if (jsonObj.has("excuteResult"))
				{
					JSONArray jsA = jsonObj.getJSONArray("excuteResult");
					for (int i = 0; i < jsA.length(); i++)
					{
						JSONObject Obj = jsA.getJSONObject(i);
						obj = pClazz.newInstance();
						for (int j = 0; j < fields.length; j++)
						{
							Field f = fields[j];
							boolean flag = f.isAccessible();
							f.setAccessible(true);
							try
							{
								// Ԫ�ش���
								if (Obj.has(f.getName().toString()) && Obj.getString(f.getName().toString()) != null)
								{
									if (Obj.getString(f.getName().toString()).toString().equals("anyType{}"))// ֵΪ��
										f.set(obj, null);// ����ֵ�������ֵ����᷵��anyType{}��Ϊ��Ԫ�ر�ǩ��ֵ
									else
										f.set(obj, Obj.getString(f.getName()).toString());// f�Ĵ�Сдһ��Ҫ��xml��ǩ��һ��
								}
							} catch (Exception e)
							{
								throw e;
							}
							f.setAccessible(flag);
						}
						Fieldlist.add(obj);
					}
				}

			}

		} catch (Exception ex)
		{
			Fieldlist = null;
			throw ex;
		}
		return Fieldlist;
	}

	

	/**
	 * 上传图片至服务器
	 * 
	 * @param bmp
	 *            图片文件 Bitmap
	 * @param pParaName
	 *            图片名称 String
	 * @return List
	 * @throws Exception
	 */
	public Object uploadSoapBytes(Bitmap bmp, String pParaName) throws Exception
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, out);
		byte[] raw = out.toByteArray();
		HashMap map = new HashMap();
		map.put("pBytes", pParaName);
		map.put("pName", pParaName);
		Gson gson = new Gson();
		String jsonTxt = gson.toJson(map);
		String result = null;
		result = NetTool.sendTxt(Url, jsonTxt, "UTF-8");
		return result;
	}

	public static java.util.Date ConverToDate(String strDate) throws Exception
	{
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.parse(strDate);
	}

	public static String ConverToString(java.util.Date date)
	{
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

		return df.format(date);
	}
}
