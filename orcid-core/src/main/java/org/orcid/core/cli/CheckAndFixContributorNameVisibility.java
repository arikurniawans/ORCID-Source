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
package org.orcid.core.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.jaxb.model.message.Contributor;
import org.orcid.jaxb.model.message.ContributorOrcid;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.WorkContributors;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.utils.NullUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 
 * @author Will Simpson
 * 
 */
public class CheckAndFixContributorNameVisibility {

    private static Logger LOG = LoggerFactory.getLogger(CheckAndFixContributorNameVisibility.class);

    private OrcidProfileManager orcidProfileManager;
    private ProfileDao profileDao;
    private TransactionTemplate transactionTemplate;
    @Option(name = "-f", usage = "Path to file containing ORCIDs to resave")
    private File fileToLoad;
    @Option(name = "-o", usage = "ORCID to resave")
    private String orcid;

    public static void main(String[] args) throws IOException {
        CheckAndFixContributorNameVisibility fixer = new CheckAndFixContributorNameVisibility();
        CmdLineParser parser = new CmdLineParser(fixer);
        try {
            parser.parseArgument(args);
            fixer.validateArgs(parser);
            fixer.init();
            fixer.execute();
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }
    }

    private void validateArgs(CmdLineParser parser) throws CmdLineException {
        if (NullUtils.allNull(fileToLoad, orcid)) {
            throw new CmdLineException(parser, "At least one of -f | -o must be specificed");
        }
    }

    public void execute() throws IOException {
        if (fileToLoad != null) {
            processFile();
        }
        if (orcid != null) {
            processOrcid(orcid);
        }
    }

    private void processFile() throws IOException {
        long startTime = System.currentTimeMillis();
        int doneCount = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(fileToLoad))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                if (StringUtils.isNotBlank(line)) {
                    processOrcid(line.trim());
                    doneCount++;
                }
            }
            long endTime = System.currentTimeMillis();
            String timeTaken = DurationFormatUtils.formatDurationHMS(endTime - startTime);
            LOG.info("Finished checking and fixing profiles: doneCount={}, timeTaken={} (H:m:s.S)", doneCount, timeTaken);
        }
    }

    private void processOrcid(final String orcid) {
        LOG.info("Checking and fixing profile: {}", orcid);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                OrcidProfile orcidProfile = orcidProfileManager.retrieveOrcidProfile(orcid);
                OrcidWorks orcidWorks = orcidProfile.retrieveOrcidWorks();
                if (orcidWorks != null) {
                    for (OrcidWork orcidWork : orcidWorks.getOrcidWork()) {
                        WorkContributors workContributors = orcidWork.getWorkContributors();
                        if (workContributors != null) {
                            for (Contributor contributor : workContributors.getContributor()) {
                                ContributorOrcid contributorOrcid = contributor.getContributorOrcid();
                                if (contributorOrcid != null) {
                                    String orcid = contributorOrcid.getPath();
                                    ProfileEntity contributorProfile = profileDao.find(orcid);
                                    if(contributorProfile.getRecordNameEntity() != null && contributorProfile.getRecordNameEntity().getVisibility() != null) {
                                        if (!Visibility.PUBLIC.value().equals(contributorProfile.getRecordNameEntity().getVisibility().value())) {
                                            contributor.setCreditName(null);
                                        }
                                    } else {
                                        if (!Visibility.PUBLIC.value().equals(contributorProfile.getNamesVisibility().value())) {
                                            contributor.setCreditName(null);
                                        }
                                    }
                                    
                                }
                            }
                        }
                    }
                }
                orcidProfileManager.updateOrcidProfile(orcidProfile);
            }
        });
    }

    @SuppressWarnings("resource")
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        orcidProfileManager = (OrcidProfileManager) context.getBean("orcidProfileManager");
        profileDao = (ProfileDao) context.getBean("profileDao");
        transactionTemplate = (TransactionTemplate) context.getBean("transactionTemplate");
    }

}
