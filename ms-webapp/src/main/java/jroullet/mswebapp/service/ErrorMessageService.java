package jroullet.mswebapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import org.springframework.stereotype.Component;

@Component
public class ErrorMessageService {

    /**
     * Get user-friendly error message for session operations based on HTTP status
     */
    public String getSessionOperationErrorMessage(int httpStatus, String operation) {
        return switch (httpStatus) {
            case 422 -> switch (operation) {
                case "enroll" -> "Inscription impossible : session complète ou délai dépassé";
                case "cancel" -> "Annulation impossible : délai de 48h dépassé";
                case "modify" -> "Modification impossible : des participants sont inscrits";
                default -> "Opération impossible : règle métier violée";
            };
            case 409 -> switch (operation) {
                case "enroll" -> "Vous êtes déjà inscrit(e) à cette session";
                case "cancel" -> "Vous n'êtes pas inscrit(e) à cette session";  // ← AJOUTER
                case "create" -> "Conflit d'horaire avec une session existante";
                default -> "Conflit détecté";
            };
            case 404 -> "Session non trouvée";
            case 403 -> "Accès non autorisé";
            case 400 -> "Données invalides";
            default -> "Erreur lors de l'opération";
        };
    }

    public String getAdminOperationErrorMessage(int httpStatus, String operation) {
        return switch (httpStatus) {
            case 403 -> "Accès administrateur requis";
            case 409 -> "Conflit lors de l'opération";
            case 404 -> "Ressource non trouvée";
            case 422 -> "Opération impossible : règle métier violée";
            case 400 -> "Données invalides";
            default -> "Erreur lors de l'opération administrative";
        };
    }

    /**
     * Get user-friendly error message for credit operations
     */
    public String getCreditOperationErrorMessage(int httpStatus, String operation) {
        return switch (httpStatus) {
            case 422 -> switch (operation) {
                case "deduct" -> "Crédits insuffisants";
                case "refund" -> "Remboursement impossible";
                default -> "Opération crédits impossible";
            };
            case 404 -> "Utilisateur non trouvé";
            case 400 -> "Données invalides";
            default -> "Erreur lors de l'opération crédits";
        };
    }

    /**
     * Extract error message from FeignException response if available
     */
    public String extractErrorMessage(FeignException e) {
        try {
            if (e.contentUTF8() != null && !e.contentUTF8().isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode response = objectMapper.readTree(e.contentUTF8());

                // Try to get message field
                if (response.has("message")) {
                    return response.get("message").asText();
                }
            }
        } catch (Exception ex) {
            // Ignore JSON parsing errors, fall back to status-based message
        }
        return null;
    }
}
