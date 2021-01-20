<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  
		xmlns:bus="http://fcl.crs/xmlns/integration/esb/1.0/event/business">
	<xsl:template match="bus:Business">
		<html>
			<body>
				<h2>Business Exception</h2>
				<xsl:apply-templates select="bus:Activity[@bus:name = 'LocalEnvironment.File.Name']"/>
				<xsl:apply-templates select="bus:Activity[@bus:name = 'CustomerNo']"/>
				<xsl:apply-templates select="bus:Activity[@bus:name = 'EosConfNo']"/>
				<xsl:apply-templates select="bus:Activity[@bus:name = 'Invalid.Count']"/>
				<table border="1">
					<tr bgcolor="#9acd32" >
						<th>Index</th>
						<th>Name</th>
						<th>Value</th>
					</tr>
					<xsl:apply-templates select="bus:Activity[@bus:name = 'Invalid.Line']"/>
				</table>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="bus:Activity[@bus:name = 'Invalid.Line']">
		<xsl:for-each select="@bus:name">
			<tr style="color: red;">
				<td><xsl:value-of select="../@bus:index"/></td>
				<td><xsl:value-of select="."/></td>
				<td><xsl:value-of select="../@bus:value"/></td>
			</tr>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="bus:Activity[@bus:name = 'LocalEnvironment.File.Name']">
		<table>
			<tr bgcolor="#9acd32">
				<td style="color: blue;">LocalEnvironment.File.Name:</td>
				<td><xsl:value-of select="@bus:value"/></td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template match="bus:Activity[@bus:name = 'CustomerNo']">
		<table>
			<tr bgcolor="#9acd32">
				<td style="color: blue;">CustomerNo:</td>
				<td><xsl:value-of select="@bus:value"/></td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template match="bus:Activity[@bus:name = 'EosConfNo']">
		<table>
			<tr bgcolor="#9acd32">
				<td style="color: blue;">EosConfNo:</td>
				<td><xsl:value-of select="@bus:value"/></td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template match="bus:Activity[@bus:name = 'Invalid.Count']">
		<table>
			<tr bgcolor="#9acd32">
				<td style="color: blue;">Invalid.Count:</td>
				<td><xsl:value-of select="@bus:value"/></td>
			</tr>
		</table>
	</xsl:template>

</xsl:stylesheet>