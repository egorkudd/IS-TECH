package is.technologies.exceptions;

public class ConsoleProgramException extends RuntimeException {
    private ConsoleProgramException(String message) {
        super(message);
    }

    public static ConsoleProgramException incorrectBankName(String name) {
        return new ConsoleProgramException("%s was used".formatted(name));
    }

    public static ConsoleProgramException incorrectPassport(String passport) {
        return new ConsoleProgramException(passport);
    }

    public static ConsoleProgramException incorrectAddress(String address) {
        return new ConsoleProgramException(address);
    }

    public static ConsoleProgramException incorrectInput(String input) {
        return new ConsoleProgramException(input);
    }
}