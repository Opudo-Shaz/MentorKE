package app.security;

import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;

public interface MentorKeIdentityStore11 {
    CredentialValidationResult validate(
            UsernamePasswordCredential credential);
}
