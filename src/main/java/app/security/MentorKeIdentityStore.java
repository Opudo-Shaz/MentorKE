package app.security;

import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;

public interface MentorKeIdentityStore {
    CredentialValidationResult validate(
            UsernamePasswordCredential credential);
}
