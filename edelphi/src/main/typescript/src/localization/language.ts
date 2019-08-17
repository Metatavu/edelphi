/**
 * Returns a meta tag value
 * 
 * @param name meta name
 * @return value or null if not found
 */
const getMetaValue = (name: string): string | null => {
  const metas = document.getElementsByTagName("meta");
  
  for (let i = 0; i < metas.length; i++) {
    if (metas[i].getAttribute('name') === name || metas[i].getAttribute('http-equiv') === name) {
      return metas[i].getAttribute('content');
    }
  }

  return null;

}

/**
 * Resolves language
 * 
 * @return langauge
 */
const getLanguage = (): string => {
  if (typeof (window as any).getLocale == "function") {
    return (window as any).getLocale().getLanguage();
  }

  const contentLanguage = getMetaValue("Content-language");
  return contentLanguage || "en";
}

export default getLanguage;