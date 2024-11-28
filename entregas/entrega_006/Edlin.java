import java.util.Scanner;

class Edlin {
    public static void main(String[] args) {
        int activeLine[] = { 1 };
        String document[] = {
                "Bienvenidos al editor EDLIN",
                "Utilice el menu inferior para editar el texto",
                "------",
                "[L] permite definir la linea activa",
                "[E] permite editar la linea activa",
                "[I] permite intercambiar dos lineas",
                "[B] borra el contenido de la linea activa",
                "[U] deshacer la última acción",
                "[R] rehacer la última acción",
                "[S] sale del programa",
                "",
                ""
        };

        Stack history = new Stack(10, document.length); // Pila con capacidad para 10 estados
        saveState(history, document);
        Stack redoStack = new Stack(10, document.length); // Pila para rehacer
        saveState(history, document);

        do {
            print(document, activeLine);
        } while (processActions(document, activeLine, history));
    }

    static void print(String[] document, int[] activeLine) {
        clearScreen();
        printHorizontalLine();
        for (int line = 0; line < document.length; line++) {
            System.out.println(line + separator(line, activeLine[0]) + document[line]);
        }
        printHorizontalLine();
    }

    static String separator(int line, int activeLine) {
        return line == activeLine ? ":*| " : ": | ";
    }

    static void printHorizontalLine() {
        System.out.println("-".repeat(50));
    }

    static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    static boolean processActions(String[] document, int[] activeLine, Stack history) {
        System.out.println("Comandos: [L]inea activa | [E]ditar | [I]ntercambiar | [B]orrar | [U]ndeshacer | [S]alir");

        switch (askChar()) {
            case 'S':   case 's':
                return false;
            case 'L':   case 'l':
                saveState(history, document);
                setActiveLine(document, activeLine);
                break;
            case 'E':   case 'e':
                saveState(history, document);
                edit(document, activeLine);
                break;
            case 'I':   case 'i':
                saveState(history, document);
                exchangeLines(document);
                break;
            case 'B':   case 'b':
                saveState(history, document);
                delete(document, activeLine);
                break;
            case 'U':   case 'u':
                undo(history, document);
                break;
            case 'R':   case 'r':
                redo(redoStack, history, document);
                break;
        }
        return true;
    }

    static char askChar() {
        Scanner input = new Scanner(System.in);
        return input.next().charAt(0);
    }

    static void delete(String[] document, int[] activeLine) {
        System.out.println("Esta acción es irreversible: indique el número de línea activa para confirmarlo [" + activeLine[0] + "]");
        if (askInt() == activeLine[0]) {
            document[activeLine[0]] = "";
        }
    }

    static void exchangeLines(String[] document) {
        int originLine, destinationLine;
        String temporaryLine;
        boolean validLine = true;

        do {
            System.out.print("Indique primera línea a intercambiar: ");
            originLine = askInt();
            validLine = originLine >= 0 && originLine < document.length;
        } while (!validLine);

        do {
            System.out.print("Indique segunda línea a intercambiar: ");
            destinationLine = askInt();
            validLine = destinationLine >= 0 && destinationLine < document.length;
        } while (!validLine);

        temporaryLine = document[destinationLine];
        document[destinationLine] = document[originLine];
        document[originLine] = temporaryLine;
    }

    static void edit(String[] document, int[] activeLine) {
        System.out.println("EDITANDO> " + document[activeLine[0]]);
        document[activeLine[0]] = askString();
    }

    static String askString() {
        Scanner input = new Scanner(System.in);
        return input.nextLine();
    }

    static void setActiveLine(String[] document, int[] activeLine) {
        boolean validLine = true;
        do {
            System.out.print("Indique la nueva línea activa: ");
            activeLine[0] = askInt();
            validLine = activeLine[0] >= 0 && activeLine[0] < document.length;
        } while (!validLine);
    }

    static int askInt() {
        Scanner input = new Scanner(System.in);
        return input.nextInt();
    }

    static void saveState(Stack history, String[] document) {
        history.push(document);
    }

    static void undo(Stack history, String[] document) {
        String[] previousState = history.pop();
        if (previousState != null) {
            for (int i = 0; i < document.length; i++) {
                document[i] = previousState[i];
            }
            System.out.println("Se ha deshecho la última acción.");
        } else {
            System.out.println("No hay acciones para deshacer.");
        }
    }
    
    static void redo(Stack redoStack, Stack history, String[] document) {
        String[] nextState = redoStack.pop();
        if (nextState != null) {
            history.push(document); // Guardar el estado actual para deshacer
            for (int i = 0; i < document.length; i++) {
                document[i] = nextState[i];
            }
            System.out.println("Se ha rehecho la última acción.");
        } else {
            System.out.println("No hay acciones para rehacer.");
        }
    }
}
