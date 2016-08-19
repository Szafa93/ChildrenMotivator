package pl.szafraniec.ChildrenMotivator.services;

public interface EmailService {
    void sendEmail(String to, String subject, String message, byte[] file, String fileName);
}
