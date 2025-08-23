package jroullet.mswebapp.exception;

import lombok.Getter;

@Getter
public class InsufficientCreditsException extends SessionValidationException {
  private final Long userId;
  private final Integer availableCredits;
  private final Integer requiredCredits;

  public InsufficientCreditsException(Long userId, Integer availableCredits, Integer requiredCredits) {
    super(String.format("User %d has insufficient credits. Available: %d, Required: %d",
            userId, availableCredits, requiredCredits));
    this.userId = userId;
    this.availableCredits = availableCredits;
    this.requiredCredits = requiredCredits;
  }


  @Override
  public String getUserMessage() {
    return "Crédits insuffisants pour vous inscrire, paiement requis";
  }
}
