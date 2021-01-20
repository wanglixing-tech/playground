package crs.fcl.integration.main;

class UserInfo implements Comparable<UserInfo> {
	private int userid;
	private int age;
	private String name;

	public UserInfo(int userid, int age, String name) {
		this.userid = userid;
		this.age = age;
		this.name = name;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.userid + "," + this.age + "," + this.name;
	}

	@Override
	public int compareTo(UserInfo o) {
		if (this.age - o.age == 0) {
			return this.userid - o.userid;
		} else {
			return this.age - o.age;
		}
	}
}
