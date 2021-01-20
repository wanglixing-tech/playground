package crs.fcl.integration.main;

import java.util.Comparator;

class MyComparator implements Comparator<UserInfo>{
	@Override
	public int compare(UserInfo o1,UserInfo o2) {
		
		if(o1.getAge()-o2.getAge()==0){
			return o1.getUserid()-o2.getUserid();
		}else{
			return o1.getAge()-o2.getAge();
		}
	}
}
