package com.mitsugaru.KarmicLog.permissions;

public enum Permission
{
	ADMIN(".admin");
	private static final String prefix = "KarmicLog";
	private String node;

	private Permission(String node)
	{
		this.node = prefix + node;
	}
	
	public String getNode()
	{
		return node;
	}

}
