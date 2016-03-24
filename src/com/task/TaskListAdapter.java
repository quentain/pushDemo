package com.task;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.push.example.R;
import com.ds.home.dutyFragment;

import java.util.HashMap;
import java.util.List;

public class TaskListAdapter extends BaseAdapter {  
		
	 /**
     * 上下文对象
     */
    private Context mContext = null;   
	private List<HashMap<String, Object>> list; 
	ViewHolder holder = null;
	
    public TaskListAdapter(Context context, List<HashMap<String, Object>> schedulList) {  
    	mContext = context;
        this.list = schedulList;
    }    
	@Override 
   public View getView(final int position, View convertView, ViewGroup parent) {        	    	
    	 if (convertView == null) {  
    		 holder = new ViewHolder();
             convertView = LayoutInflater.from(mContext).inflate(R.layout.task_item, null);  
             holder.elart = (ImageView) convertView.findViewById(R.id.elart);
             holder.post = (TextView) convertView.findViewById(R.id.post);
             holder.place = (TextView) convertView.findViewById(R.id.place);
             holder.Date = (TextView) convertView.findViewById(R.id.date);
             holder.othernote=(TextView)convertView.findViewById(R.id.othernote);
             convertView.setTag(holder);  
         } else {  
             holder = (ViewHolder) convertView.getTag();  
         } 
    	holder.post.setText("起始时间："+list.get(position).get("starttime"));//岗位
    	holder.place.setText("任务内容："+list.get(position).get("workloc"));//地点 
    	holder.Date.setText("结束时间："+list.get(position).get("endtime"));//时间
//    	if(list.get(position).get("Replace")!=null){
//    		if(!list.get(position).get("deviceid").equals(dutyFragment.deviceid)){
//        		holder.elart.setBackgroundResource(R.drawable.alarm);
//        		holder.othernote.setText("备注信息："+"顶班");//时间
//        		holder.othernote.setTextColor(Color.GREEN);
//        	}else{
//        		holder.elart.setBackgroundResource(R.drawable.alarm);
//        		holder.othernote.setText("备注信息："+"申请顶班");//时间
//        		holder.othernote.setTextColor(Color.RED);
//        	}
//    	}else{
//    		holder.elart.setBackgroundResource(R.drawable.elart_icon);
//    		holder.othernote.setText("备注信息："+"正常排班");//时间
//    		holder.othernote.setTextColor(Color.GRAY);
//    	}
        if(list.get(position).get("IsAgree").equals("0")){
            holder.elart.setBackgroundResource(R.drawable.elart_icon);
    		holder.othernote.setText("备注信息："+"正常排班");//时间
    		holder.othernote.setTextColor(Color.GRAY);
        }else if(list.get(position).get("IsAgree").equals("1")){
            if(!list.get(position).get("Replace").equals(dutyFragment.deviceid)){
                holder.elart.setBackgroundResource(R.drawable.alarm);
                holder.othernote.setText("备注信息："+"顶班申请中");//时间
                holder.othernote.setTextColor(Color.RED);
            }else{
                holder.elart.setBackgroundResource(R.drawable.alarm);
                holder.othernote.setText("备注信息："+"申请待接受");//时间
                holder.othernote.setTextColor(Color.GREEN);
            }
        }else{
            if(list.get(position).get("Replace").equals(dutyFragment.deviceid)){
                holder.elart.setBackgroundResource(R.drawable.alarm);
                holder.othernote.setText("备注信息："+"顶班申请成功");//时间
                holder.othernote.setTextColor(Color.RED);
            }else{
                holder.elart.setBackgroundResource(R.drawable.elart_icon);
        		holder.othernote.setText("备注信息："+"顶班接受");//时间
        		holder.othernote.setTextColor(Color.GREEN);
            }

        }
    	
        return convertView;  
      }  

	  /** 
     * 锟斤拷ListView锟斤拷莘锟斤拷锟戒化时,锟斤拷锟矫此凤拷锟斤拷锟斤拷锟斤拷锟斤拷ListView 
     * @param list 
     */  
    public void updateListView(List<HashMap<String, Object>> list){  
        this.list = list;  
        notifyDataSetChanged();  
    }  
	@Override     
    public int getCount() {  
        return list.size();  
    } 
	
    @Override  
    public Object getItem(int position) {  
        return list.get(position);  
    }  
  
    @Override  
    public long getItemId(int position) {  
        return position;  
    }        
    /** 
     * ViewHolder锟斤拷锟斤拷锟皆达拷锟斤拷item锟叫控硷拷锟斤拷锟斤拷锟斤拷 
     */  
    final class ViewHolder {  
        ImageView elart;
        TextView post;//岗位
        TextView Date;//日期
        TextView place;//地点
        TextView othernote;//备注
    }
	  
    /**
     * 单击事件监听器
     */
    private onRightItemClickListener mListener = null;
    
    public void setOnRightItemClickListener(onRightItemClickListener listener){
    	mListener = listener;
    }

    public interface onRightItemClickListener {
        void onRightItemClick(View v, int position);
    }
}  
