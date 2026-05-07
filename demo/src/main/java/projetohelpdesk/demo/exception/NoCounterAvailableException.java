package projetohelpdesk.demo.exception;

public class NoCounterAvailableException extends RuntimeException {

    public NoCounterAvailableException() {
        super("No counter available. All counters have reached the maximum number of active tickets.");
    }

    public NoCounterAvailableException(String message) {
        super(message);
    }

    // cassio estou implementando esse comentario para deixar vc ciente de que como eu fiz todas as alteracoes ja na main, eu nao conseguiria subir
    // a pr pois ja estava tudo na main e para conseguir subir alguma coisa explicando o que ja foi feito, eu add esse bloco comentado aqui.
}
