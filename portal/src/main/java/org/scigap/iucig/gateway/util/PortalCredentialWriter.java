package org.scigap.iucig.gateway.util;

import org.apache.airavata.common.utils.DBUtil;
import org.apache.airavata.credential.store.credential.Credential;
import org.apache.airavata.credential.store.store.CredentialStoreException;
import org.apache.airavata.credential.store.store.impl.db.CredentialsDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Credential writer implementation for BR2
 */
public class PortalCredentialWriter implements org.apache.airavata.credential.store.store.CredentialWriter {
    public static final String GATEWAY_ID  = "default";
    private CredentialsDAO credentialsDAO;
    private DBUtil dbUtil;
    protected static Logger log = LoggerFactory.getLogger(PortalCredentialWriter.class);

    public PortalCredentialWriter(DBUtil dbUtil) {
        this.dbUtil = dbUtil;
        credentialsDAO = new CredentialsDAO();
    }

    @Override
    public void writeCredentials(Credential credential) throws CredentialStoreException {
        Connection connection = null;
        try {
            BR2Credential credentialStore = (BR2Credential)credential;
            connection = dbUtil.getConnection();
            credentialsDAO.addCredentials(GATEWAY_ID, credentialStore, connection);
            if (!connection.getAutoCommit()) {
                connection.commit();
            }
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    log.error("Unable to rollback transaction", e1);
                }
            }
            throw new CredentialStoreException("Unable to retrieve database connection.", e);
        } finally {
            DBUtil.cleanup(connection);
        }
    }
}
