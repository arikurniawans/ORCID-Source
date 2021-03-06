<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2014 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->
<@public classes=['home'] nav="consortia-list">
    <div class="member-list row">        
        <div class="col-md-9 col-md-offset-3 col-sm-12 col-xs-12">
            <h1>ORCID Consortia Members</h1>
            <p>Consortia are groups of 5 or more non-profit and/or governmental organizations organizations taking a coordinated approach to ORCID implementation.
			Interested in joining ORCID as a consortium? <a href="<@orcid.rootPath '/about/membership'/>">Learn more about membership</a></p>
            <div ng-controller="ConsortiaListController">
                <div ng-hide="membersListSrvc.consortiaList != null" class="text-center">
                    <i class="glyphicon glyphicon-refresh spin x4 green" id="spinner"></i>
                    <!--[if lt IE 8]>
                        <img src="${staticCdn}/img/spin-big.gif" width="85" height ="85"/>
                    <![endif]-->
                </div>
                <div ng-if="membersListSrvc.consortiaList" ng-cloak>
                    <p>There are currently {{membersListSrvc.consortiaList.length}} ORCID consortia members.</p>
                    <p>
	            		<a href="<@orcid.rootPath '/members'/>">All members</a> | <a class="selected" href="<@orcid.rootPath '/consortia'/>">Consortia members</a>
	            	</p>
                    <div class="member" ng-repeat="member in membersListSrvc.consortiaList | orderBy : 'name'">
                        <hr class="no-margin-top" />
	                    	<div class="col-md-12 col-sm-12 col-xs-12">
	                        	<h2 ng-bind="member.name" ng-cloak></h2>	                        
	                        	<p ng-cloak>{{member.researchCommunity}} | {{member.country}}</p>
	                        </div>
	                        <div class="col-md-10 col-sm-10 col-xs-12">
	                        	<p>
		                        	<img class="member-logo" src="{{member.logoUrl}}"  ng-cloak ng-if="member.logoUrl">
		                        	<span class="member-decsription" ng-bind-html="renderHtml(member.description)" ng-if="member.description" ng-cloak></span>
	                        	</p>
	                        	<p class="clear-fix">
	                        		<a ng-href="{{membersListSrvc.getMemberPageUrl(member.slug)}}" ng-cloak>Member details <i class="glyphicon x075 glyphicon-chevron-right"></i></a>
	                        	</p>
	                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</@public>