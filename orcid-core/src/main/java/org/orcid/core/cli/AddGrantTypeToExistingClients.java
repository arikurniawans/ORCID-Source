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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.persistence.jpa.entities.ClientAuthorisedGrantTypeEntity;
import org.orcid.persistence.jpa.entities.OrcidClientDetailsEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 
 * @author Angel Montenegro
 * 
 * */
public class AddGrantTypeToExistingClients {

    
    @Option(name = "-g", usage = "Grant types to add (like 'client_credentials', 'refresh_token' and 'authorization_code')")
    private String newGrantTypes;
    
    @Option(name = "-t", usage = "CSV client types, must be one in ClientType, if null, assume the change applies to all client types")
    private String clientTypes;
    
    private Set<String> grantTypes = new HashSet<String>();
    
    private Set<ClientType> allowedClientTypes = new HashSet<ClientType>();
    
    private ClientDetailsManager clientDetailsManager;
    private TransactionTemplate transactionTemplate;

    private int clientsUpdated = 0;    
    
    public static void main(String [] args) {
        AddGrantTypeToExistingClients addScopesToExistingClients = new AddGrantTypeToExistingClients();
        CmdLineParser parser = new CmdLineParser(addScopesToExistingClients);
        try {           
            parser.parseArgument(args);
            addScopesToExistingClients.validateParameters(parser);
            addScopesToExistingClients.init();
            addScopesToExistingClients.process();
            System.out.println();
            System.out.println();
            System.out.println(addScopesToExistingClients.getClientsUpdated() + " clients were updated");
            System.out.println();
            System.out.println();
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            System.exit(1);
        }        
        System.exit(0);
    }
    
    @SuppressWarnings("resource")
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        clientDetailsManager = (ClientDetailsManager) context.getBean("clientDetailsManager");
        transactionTemplate = (TransactionTemplate) context.getBean("transactionTemplate");
    }
    
    public void process() {         
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                List<OrcidClientDetailsEntity> clients = clientDetailsManager.getAll();
                for (OrcidClientDetailsEntity client : clients) {
                    // Only updater clients should be updated
                    if (isInAllowedClientTypes(client)) {
                        OrcidClientDetailsEntity clientDetails = clientDetailsManager.findByClientId(client.getId());
                        updateGrantTypes(clientDetails);
                    }
                }
            }
        });
    }
    
    private boolean isInAllowedClientTypes(OrcidClientDetailsEntity client) {
        //Ignore the public client
        if(client.getClientType() == null) {
            return false;
        }
        
        if(allowedClientTypes == null || allowedClientTypes.isEmpty())
            return true;
        
        for(ClientType clientType : allowedClientTypes) {
            if(clientType.equals(client.getClientType()))
                return true;
        }
        
        return false;
    }
    
    private void updateGrantTypes(OrcidClientDetailsEntity clientDetails) {        
        for(String grantType : grantTypes) {            
            boolean alreadyHaveGrantType = false;
            for (String existingGrantType : clientDetails.getAuthorizedGrantTypes()) {
                if (grantType.equals(existingGrantType)) {
                    alreadyHaveGrantType = true;
                    break;
                }
            }

            if (!alreadyHaveGrantType) {
                ClientAuthorisedGrantTypeEntity newGrantType = new ClientAuthorisedGrantTypeEntity();
                newGrantType.setGrantType(grantType);
                newGrantType.setClientDetailsEntity(clientDetails);
                clientDetails.getClientAuthorizedGrantTypes().add(newGrantType);
                
                clientDetailsManager.merge(clientDetails);
                clientsUpdated += 1;
                System.out.println("Client " + clientDetails.getId() + " has been updated");                
            } else {
                System.out.println("Client " + clientDetails.getId() + " already have the " + grantType + " scope");
            }
        }                
    }        
    
    public void validateParameters(CmdLineParser parser) throws CmdLineException {
        if(PojoUtil.isEmpty(newGrantTypes)) {
            throw new CmdLineException(parser, "-s parameter must not be null");
        } else {
            String [] grantTypesArray = newGrantTypes.split(",");
            for(String grantType : grantTypesArray) {
                grantType = grantType.trim();                 
                if(!grantType.equals("refresh_token") && !grantType.equals("authorization_code") && !grantType.equals("client_credentials")) {
                    throw new CmdLineException(parser, "Invalid grantType: " + grantType);
                }
                if(!PojoUtil.isEmpty(grantType)){
                    grantTypes.add(grantType);
                }                                                       
            }
        }
        
        if(!PojoUtil.isEmpty(clientTypes)) {
            String [] clientTypesArray = clientTypes.split(",");
            for(String clientType : clientTypesArray) {
                try {
                    allowedClientTypes.add(ClientType.fromValue(clientType));
                } catch(IllegalArgumentException ie) {
                    throw new CmdLineException(parser, "Invalid client type: " + clientType);
                }
            }            
        }
    }

    public int getClientsUpdated() {
        return clientsUpdated;
    }            
}
