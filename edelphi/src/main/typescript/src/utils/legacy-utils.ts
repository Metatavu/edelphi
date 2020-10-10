import { CommandEvent } from "../types";

export default class LegacyUtils {

  public static addCommandListener(listener: (event: CommandEvent) => void) {    
    (document as any).addEventListener("react-command", listener);
  }

  public static removeCommandListener(listener: (event: CommandEvent) => void) {    
    (document as any).removeEventListener("react-command", listener);
  }

}
