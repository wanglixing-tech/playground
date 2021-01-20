package crs.fcl.integration.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompareTest {
	public static void main(String[] args) {
		List<UserInfo> list = new ArrayList<UserInfo>();
		list.add(new UserInfo(1,21,"name1"));
		list.add(new UserInfo(2,27,"name1"));
		list.add(new UserInfo(3,15,"name1"));
		list.add(new UserInfo(5,24,"name1"));
		list.add(new UserInfo(4,24,"name1"));
		Collections.sort(list);
		for(int i=0;i<list.size();i++){
			System.out.println(list.get(i));
		}
	}
}
