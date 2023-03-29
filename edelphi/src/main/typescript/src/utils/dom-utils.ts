namespace DomUtils {

  /**
   * Returns attribute with given name from element or null if not found
   * 
   * @param element element
   * @param attributeName attribute name
   * @returns value or null if not found
   */
  export const getAttribute = (element: Element, attributeName: string): string | null => {
    if (!element) {
      return null;
    }
  
    const attribute = element.attributes.getNamedItem(attributeName);
    if (!attribute) {
      return null;
    }
  
    return attribute.value;
  }
  
  /**
   * Returns number attribute with given name from element or null if not found or if value is not a number
   * 
   * @param element element
   * @param attributeName attribute name
   * @returns value or null if not found or if value is not a number
   */
  export const getIntAttribute = (element: Element, attributeName: string): number | null => {
    const value = getAttribute(element, attributeName);
    return value ? parseInt(value) : null;
  }
  
  /**
   * Returns boolean attribute with given name from element. Returns true if attribute value is "true" and false otherwise
   * 
   * @param element element
   * @param attributeName element attribute name
   * @returns boolean attribute value
   */
  export const getBoolAttribute = (element: Element, attributeName: string): boolean => {
    const value = getAttribute(element, attributeName);
    return value === "true";
  }

};

export default DomUtils;