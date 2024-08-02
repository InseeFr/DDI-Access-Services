<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"     
    xmlns:uuid="http://some.namespace.com"
    xmlns:r="ddi:reusable:3_3"
    xmlns:ddi="ddi:instance:3_3"
    exclude-result-prefixes="xs uuid"    
    version="3.0">
    <xsl:param name="idValue"/>
    <xsl:param name="suggesterName"/>
    <xsl:param name="suggesterDescription"/>
    <xsl:param name="version"/>
    <xsl:param name="timbre"/>
    <xsl:output indent="yes"/>     
    
    <xsl:template match="data">
                       
        <xsl:variable name="filename">
            <xsl:value-of select="string-join((tokenize(tokenize(document-uri(/),'/')[last()],'\.')[position()!=last()]),'.')"/>
        </xsl:variable>                       
       
        <!-- Creation du xml a partir du json -->
        <xsl:variable name="xmlFROMjson">
            <xsl:copy-of select="json-to-xml(.)"/>
        </xsl:variable>
        <!--<xsl:text>xmlFROMjson</xsl:text>
        <xsl:copy-of select="$xmlFROMjson"></xsl:copy-of>-->
        <!-- Enrichissement avec les uuid -->
        <xsl:variable name="xmlFROMjsonUUID">
            <xsl:apply-templates select="$xmlFROMjson" mode="uuid"/>
        </xsl:variable>
        <!--<xsl:text>xmlFROMjsonUUID</xsl:text>
        <xsl:copy-of select="$xmlFROMjsonUUID"></xsl:copy-of>
        -->
        <!-- Creation du ddi -->
        <xsl:apply-templates select="$xmlFROMjsonUUID">
            <xsl:with-param name="filename" select="$filename"/>
        </xsl:apply-templates>
              
    </xsl:template>
    
    <!-- Templates pour l'enrichissement uuid -->
    
    <xsl:template match="*[local-name()='array']" mode="uuid">
        <xsl:variable name="uuid" select="uuid:randomUUID()"/>
        <array>
            <uuidArray><xsl:value-of select="$uuid"/></uuidArray>
            <xsl:apply-templates mode="uuid"/>
        </array>
    </xsl:template>
    
    <xsl:template match="*[local-name()='map']" mode="uuid">
        <xsl:variable name="uuidCode" select="uuid:randomUUID()"/>
        <!--<xsl:variable name="uuidFragment" select="uuid:randomUUID()"/>-->
        <map>
            <uuidCode><xsl:value-of select="$uuidCode"/></uuidCode>
            <uuidFragment><xsl:value-of select="uuid:randomUUID()"/></uuidFragment>
            <xsl:apply-templates mode="uuid"/>            
        </map>
    </xsl:template>
    
    <xsl:template match="*[local-name()='string']" mode="uuid">
        <xsl:variable name="uid" select="uuid:randomUUID()"/>
        <string>
            <xsl:copy-of select="@*"></xsl:copy-of>            
            <xsl:apply-templates mode="uuid"/>            
        </string>
    </xsl:template>
    
    <!-- Template pour creation ddi -->
    
    <xsl:template match="array">       
        <xsl:param name="filename"/>
        <xsl:element name="ddi:FragmentInstance">
            <xsl:namespace name="r" select="'ddi:reusable:3_3'"/>
            <TopLevelReference>
                <Agency>fr.insee</Agency>
                <ID><xsl:value-of select="uuidArray"/></ID>
                <Version><xsl:value-of select="$version"/></Version>
                <TypeOfObject>CodeList</TypeOfObject>
            </TopLevelReference>
            <Fragment xmlns="ddi:instance:3_3">
                <xsl:namespace name="r" select="'ddi:reusable:3_3'"/>
                <CodeList isUniversallyUnique="true" xmlns="ddi:logicalproduct:3_3" >  <!--xmlns="ddi:logicalproduct:3_3"-->
                    <xsl:attribute name="versionDate" select="concat(translate(substring(xs:string(current-dateTime()),1,23),'+','0'),'0000Z')"></xsl:attribute>
                    <r:URN><xsl:value-of select="concat('urn:ddi:fr.insee:',uuidArray/text(),':',$version)"/></r:URN>
                    <r:Agency>fr.insee</r:Agency>
                    <r:ID><xsl:value-of select="uuidArray"/></r:ID>                    
                    <r:Version><xsl:value-of select="$version"/></r:Version>
                    <r:UserID typeOfUserID="colectica:sourceId">INSEE-<xsl:value-of select="$timbre"/></r:UserID>
                    <CodeListName>
                        <r:String xml:lang="fr-FR">SUGGESTER_<xsl:value-of select="$idValue"/></r:String>
                    </CodeListName>
                    <r:Label>
                        <r:Content xml:lang="fr-FR"><xsl:value-of select="$suggesterName"/></r:Content>
                    </r:Label>
                    <r:Description>
                        <r:Content xml:lang="fr-FR"><xsl:value-of select="$suggesterDescription"/></r:Content>
                    </r:Description>
                    <xsl:apply-templates select="map" mode="code"/>
                </CodeList>
            </Fragment>
            
            <xsl:apply-templates select="map" mode="fragment"/>
            
        </xsl:element>       
    </xsl:template>
    
    <xsl:template match="map" mode="code"> 
        <xsl:param name="filename"/>
        <Code xmlns="ddi:logicalproduct:3_3" isUniversallyUnique="true">  <!--xmlns="ddi:logicalproduct:3_3"-->
            <r:URN><xsl:value-of select="concat('urn:ddi:fr.insee:',uuidCode/text(),':',$version)"/></r:URN>
            <r:Agency>fr.insee</r:Agency>
            <r:ID><xsl:value-of select="uuidCode"/></r:ID>                    
            <r:Version><xsl:value-of select="$version"/></r:Version>
            <!-- <r:UserID typeOfUserID="colectica:sourceId">INSEE-<xsl:value-of select="$filename"/><xsl:value-of select="0 + position()"/></r:UserID> -->
            <r:CategoryReference>
                <r:Agency>fr.insee</r:Agency>
                <r:ID><xsl:value-of select="uuidFragment"/></r:ID> 
                <r:Version><xsl:value-of select="$version"/></r:Version>
                <r:TypeOfObject>Category</r:TypeOfObject>
            </r:CategoryReference>
            <r:Value><xsl:value-of select="string[@key='id']"/></r:Value>
        </Code>
    </xsl:template>
    
    <xsl:template match="map" mode="fragment">             
        <Fragment xmlns:r="ddi:reusable:3_3" xmlns="ddi:instance:3_3"> <!-- xmlns:r="ddi:reusable:3_3" -->
            <Category isUniversallyUnique="true" isMissing="false" xmlns="ddi:logicalproduct:3_3"> <!-- xmlns="ddi:logicalproduct:3_3"-->
                <xsl:attribute name="versionDate" select="concat(translate(substring(xs:string(current-dateTime()),1,23),'+','0'),'0000Z')"></xsl:attribute>
                <r:URN><xsl:value-of select="concat('urn:ddi:fr.insee:',uuidFragment/text(),':',$version)"/></r:URN>
                <r:Agency>fr.insee</r:Agency>
                <r:ID><xsl:value-of select="uuidFragment"/></r:ID>
                <r:Version><xsl:value-of select="$version"/></r:Version>
                <!--<r:UserID typeOfUserID="colectica:sourceId">INSEE-<xsl:value-of select="0 + position()"/></r:UserID> -->
               <CategoryName>
                   <r:String xml:lang="fr-FR"><xsl:value-of select="concat(string-join((tokenize(tokenize(document-uri(/),'/')[last()],'\.')[position()!=last()]),'.'), string[@key='id'])"/></r:String>
                </CategoryName>
                <r:Label>
                    <r:Content xml:lang="fr-FR"><xsl:value-of select="string[@key='label']"/></r:Content>
                </r:Label>
            </Category>
        </Fragment>
    </xsl:template>
    
    
    
</xsl:stylesheet>