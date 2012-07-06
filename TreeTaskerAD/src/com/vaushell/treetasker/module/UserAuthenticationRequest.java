package com.vaushell.treetasker.module;

public class UserAuthenticationRequest
{
	// PUBLIC
	public UserAuthenticationRequest()
	{
		init();
	}

	public UserAuthenticationRequest( String login,
	                                  String password )
	{
		this.login = login;
		this.password = password;
	}

	public String getLogin()
	{
		return login;
	}

	public void setLogin( String login )
	{
		this.login = login;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword( String password )
	{
		this.password = password;
	}

	// PROTECTED
	// PRIVATE
	private String	login;
	private String	password;

	private void init()
	{

	}
}
