package crs.fcl.integration.main;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ComparatorTest {
	public static void main(String[] args) {
		List<UserInfo> list = new ArrayList<UserInfo>();
		list.add(new UserInfo(1,21,"name1"));
		list.add(new UserInfo(2,27,"name2"));
		list.add(new UserInfo(3,15,"name3"));
		list.add(new UserInfo(5,24,"name4"));
		list.add(new UserInfo(4,24,"name5"));
		MyComparator comparator = new MyComparator();
		Collections.sort(list,comparator);
		for(int i=0;i<list.size();i++){
			System.out.println(list.get(i));
		}
	}
}
