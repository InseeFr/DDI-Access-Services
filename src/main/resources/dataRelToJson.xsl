<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:msxsl="urn:schemas-microsoft-com:xslt" exclude-result-prefixes="msxsl"
    xmlns:l="ddi:logicalproduct:3_3"
    xmlns:r="ddi:reusable:3_3"
    >
    <xsl:output method="text" omit-xml-declaration="yes" encoding="UTF-8"/>
    
    <xsl:template match="node() | @*">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"> </xsl:apply-templates>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="/">
        <!--    <xsl:template match="//l:DataRelationship">-->
        
        
        <!--        xsl variable dataRelation_URN
        xsl variable dataRelation_name
        xsl variable dataRelation_libelle_fr , avec xpath pour avoir le francais-->
        
        
        <xsl:text>[</xsl:text>
        
        
        
        <xsl:for-each select="//l:DataRelationship">
            <xsl:variable name="dataRelation_URN" select="r:URN"/> 
            <xsl:variable name="dataRelation_name" select="'./l:DataRelationshipName/r:String'"></xsl:variable>
            <xsl:variable name="dataRelation_libelle_fr" select="'./r:Label'"></xsl:variable>
            <xsl:if test="position()>1">
                <xsl:text>,</xsl:text>
            </xsl:if>
            <xsl:text>{</xsl:text>
            <!--        <TEXT>  "id": "<value of URN>",</TEXT>-->            
            
            
            <xsl:text>"id": "</xsl:text>
            <xsl:value-of select="./r:URN"/>
            <xsl:text>", "nom": "</xsl:text>
            <xsl:value-of select="./l:DataRelationshipName/r:String"/>
            <xsl:text>", "label": 
            [
            {
            "contenu": "</xsl:text>
            <xsl:value-of select="normalize-space(./r:Label/r:Content)"/>
            <xsl:text>", "langue": "fr"</xsl:text>
            
            <xsl:text>},
            {
            "contenu": "",
            "langue": "en"
            }
            ]</xsl:text>
            
            <xsl:text>,
            "variables":
            [</xsl:text>     <!--ouverture accolade variables-->
            
            <!--            ici for each variable used reference -->
            
            <xsl:for-each select="./l:LogicalRecord/l:VariablesInRecord/l:VariableUsedReference">
                <xsl:variable name="VariableUsed_ID" select="r:ID"/>
                
                <xsl:if test="position()>1">
                    <xsl:text>,</xsl:text>
                </xsl:if>
                <xsl:text>{</xsl:text>      <!--ouverture variable-->
                
                <!--"id": "urn:ddi:fr.insee:4b7464c5-90e9-4dbb-858d-120476bf441a:2",
                "nom": "REGION",-->
                
                <xsl:text>"id": "</xsl:text>
                <xsl:value-of select="//l:Variable[r:ID=$VariableUsed_ID]/r:URN"/>
                <xsl:text>", "nom": "</xsl:text>
                <xsl:value-of select="normalize-space(//l:Variable[r:ID=$VariableUsed_ID]/l:VariableName/r:String)"/>
                <xsl:text>", "label": </xsl:text>
                
                <xsl:text>[
                {
                "contenu": "</xsl:text>
                <xsl:value-of select="normalize-space(//l:Variable[r:ID=$VariableUsed_ID]/r:Label/r:Content)"/>
                <xsl:text>",
                "langue": "fr"
                },
                {
                "contenu": "",
                "langue": "en"
                }
                ]</xsl:text>     <!--fermeture label ligne 86-->
                
                <xsl:text>, "ordre": "</xsl:text>
                <xsl:number/>
                <xsl:text>"</xsl:text>
                <xsl:text>, "representation": "</xsl:text>
                <!--                xsl if -->
                <xsl:variable name="NumDom" select="//l:Variable[r:ID=$VariableUsed_ID]/l:VariableRepresentation/r:NumericRepresentation/@blankIsMissingValue"/>
                <xsl:variable name="TexDom" select="//l:Variable[r:ID=$VariableUsed_ID]/l:VariableRepresentation/r:TextRepresentation/@blankIsMissingValue"/>
                <xsl:variable name="CodDom"
                    select="//l:Variable[r:ID=$VariableUsed_ID]/l:VariableRepresentation/r:CodeRepresentation/r:CodeListReference/r:TypeOfObject"/>
                <xsl:variable name="CodDate" select="//l:Variable[r:ID=$VariableUsed_ID]/l:VariableRepresentation/r:DateTimeRepresentation/@blankIsMissingValue"/>
                
                <xsl:if test="$CodDom != ''">codes</xsl:if>
                <xsl:if test="$NumDom != ''">numerique</xsl:if>
                <xsl:if test="$TexDom != ''">texte</xsl:if>
                <xsl:if test="$CodDate != ''">date</xsl:if>
                
                
                
                <xsl:text>"</xsl:text>  <!--fermeture accolade type de variable ligne 102-->
                
                <!--                partie controles pour les codes-->
                <xsl:if test="$CodDom != ''">, "controles":
                    {
                    "codes": [
                    <xsl:variable name="ID_CodeList"
                        select="//l:Variable[r:ID=$VariableUsed_ID]/l:VariableRepresentation/r:CodeRepresentation/r:CodeListReference/r:ID"/>
                    
                    
                    <xsl:for-each select="//l:CodeList[r:ID=$ID_CodeList]/l:Code">
                        <xsl:if test="position()>1">
                            <xsl:text>,</xsl:text>
                        </xsl:if>
                        
                        <!--                        "code": "B",-->
                        <xsl:text>{ "code": "</xsl:text>
                        <xsl:value-of select="./r:Value"/>
                        <xsl:text> " , "label": [</xsl:text>
                        <xsl:variable name="ID_Categorie" select="./r:CategoryReference/r:ID"/>
                        
                        <xsl:text>{
                        "langue": "fr",
                        "contenu": "</xsl:text>
                        <xsl:value-of select="normalize-space(//l:Category[r:ID=$ID_Categorie]/r:Label/r:Content)"/>
                        <xsl:text>"
                        },
                        {
                        "langue": "en",
                        "contenu": ""
                        }
                        ] <!--fermeture accolade label ligne 141-->
                        }
                       
                        </xsl:text>
                    </xsl:for-each>
                    <xsl:text>]</xsl:text>     <!--fermeture accolade codes ligne 128-->
                    <xsl:text>}</xsl:text>       <!--fermeture controles ligne 127-->                  
                </xsl:if>
                
                <!--                fin de partie controles pour les codes-->
                
                <!--                partie controles pour les textes -->
                <!--                fin de partie controles pour les textes-->
                
                <!--                partie controles pour les numériques-->
                <!--                fin de partie controles pour les numériques-->
                
                
                
                <xsl:text>}</xsl:text>       <!--fermeture variable ligne 75-->
                
                
            </xsl:for-each> <!--fin for each des variables used reference--> 
            <xsl:text>]</xsl:text>    <!-- accolade pour variable  ligne 59-->
            <xsl:text>}</xsl:text>
        </xsl:for-each>
        <xsl:text>]</xsl:text>
    </xsl:template>
</xsl:stylesheet>