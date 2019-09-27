package fi.metatavu.edelphi.domainmodel.features;

public enum Feature {

  /**
   * Allows basic usage of eDelphi
   */
  BASIC_USAGE,
  
  /**
   * Create panels
   */
  CREATE_PANELS,

  /**
   * Helpdesk
   */
  
  ACCESS_HELPDESK,
  
  /**
   * Create and edit panel queries
   */
  MANAGE_PANEL_QUERIES,
  
  /**
   * Panel management
   */
  MANAGE_PANEL,
  
  /**
   * Panel invitations management
   */
  MANAGE_PANEL_INVITATIONS,
  
  /**
   * Panel communications management 
   */
  MANAGE_PANEL_COMMUNICATION,

  /**
   * Panel bulletins and material management 
   */
  MANAGE_PANEL_MATERIALS,
  
  /**
   * Access to query activity views
   */
  
  ACCESS_PANEL_QUERY_ACTIVITY,
  
  /**
   * Access to query results view
   */
  
  ACCESS_PANEL_QUERY_RESULTS,
  
  /**
   * Reults exporting
   */
  
  ACCESS_PANEL_QUERY_EXPORT,

  /**
   * Panel query report comparison access
   */
  
  ACCESS_PANEL_REPORT_COMPARISON,

  /**
   * Timestamping
   */

  MANAGE_PANEL_TIMESTAMPS,

  /**
   * Live view
   */

  ACCESS_LIVE_VIEW,
  
  /**
   * Access to Live-Delphi
   */
  
  SERVICE_LIVE_DELPHI,
  
  /**
   * Access to Sanelukone
   */

  SERVICE_SANELUKONE
  
}