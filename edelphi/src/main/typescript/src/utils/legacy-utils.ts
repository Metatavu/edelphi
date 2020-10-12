import { CommandEvent } from "../types";

/**
 * Utility class for legacy UI integration
 */
export default class LegacyUtils {

  /**
   * Adds a react-command listener
   * 
   * @param listener react-command listener
   */
  public static addCommandListener(listener: (event: CommandEvent) => void) {    
    (document as any).addEventListener("react-command", listener);
  }

  /**
   * Removes a react-command listener
   * 
   * @param listener react-command listener
   */
  public static removeCommandListener(listener: (event: CommandEvent) => void) {    
    (document as any).removeEventListener("react-command", listener);
  }

}
