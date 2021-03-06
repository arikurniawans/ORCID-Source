/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.manager.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.net.URI;
import java.net.URISyntaxException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.orcid.core.BaseTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

/**
 * 
 * @author Will Simpson
 *
 */
public class OrcidUrlManagerTest extends BaseTest {

    @Resource
    private OrcidUrlManager orcidUrlManager;

    @Test
    public void testDetermineFullTargetUrlFromSavedRequest() throws URISyntaxException {
        // Saved urls that are OK to use
        checkSame("https://orcid.org/my-orcid");
        checkSame("https://orcid.org/account");
        checkSame("https://orcid.org/account?newlogin");
        checkSame(
                "https://orcid.org/oauth/authorize?client_id=APP-5AYWFGEWVKRWQFS3&response_type=code&scope=/orcid-profile/read-limited&redirect_uri=http://localhost:8080/orcid-web/oauth/playground");
        checkSame("https://orcid.org/verify-email/a1VGWGpmdTlPdjBHbCtCNHIxUkhST3NPUUpRQ3Q2QXpMTTVIVVl0YnFseE1OZHNLQXg2SFFRUDVHOHZMZTZRLw?lang=en");
        // Saved urls to ignore
        assertNull(determineTargetUrl("https://orcid.org/blank.gif"));
        assertNull(determineTargetUrl("https://orcid.org/oauth/custom/login.json"));
        assertNull(determineTargetUrl("https://orcid.org/shibboleth/signin/auth.json"));
    }

    private void checkSame(String savedUrl) throws URISyntaxException {
        assertEquals(savedUrl, determineTargetUrl(savedUrl));
    }

    private String determineTargetUrl(String savedUrl) throws URISyntaxException {
        Pair<HttpServletRequest, HttpServletResponse> pair = setUpSavedRequest(savedUrl);
        return orcidUrlManager.determineFullTargetUrlFromSavedRequest(pair.getLeft(), pair.getRight());
    }

    private Pair<HttpServletRequest, HttpServletResponse> setUpSavedRequest(String savedUrl) throws URISyntaxException {
        URI uri = new URI(savedUrl);
        MockHttpServletRequest savedRequest = new MockHttpServletRequest("GET", uri.getPath());
        savedRequest.setScheme(uri.getScheme());
        savedRequest.setServerName(uri.getHost());
        savedRequest.setQueryString(uri.getQuery());
        MockHttpServletResponse savedResponse = new MockHttpServletResponse();
        HttpSessionRequestCache sessionCache = new HttpSessionRequestCache();
        sessionCache.saveRequest(savedRequest, savedResponse);

        MockHttpServletRequest currentRequest = new MockHttpServletRequest();
        currentRequest.setSession(savedRequest.getSession());
        MockHttpServletResponse currentResponse = new MockHttpServletResponse();
        return new ImmutablePair<>(currentRequest, currentResponse);
    }
}
