import { AppAction } from '../actions';
import { StoreState } from '../types';
import { ACCESS_TOKEN_UPDATE, QUERY_VALIDATION_MESSAGE_UPDATE } from '../constants';

export function reducer(storeState: StoreState, action: AppAction): StoreState {
  switch (action.type) {
    case ACCESS_TOKEN_UPDATE:
      const accessToken = action.accessToken;
      return {...storeState, accessToken: accessToken };
    case QUERY_VALIDATION_MESSAGE_UPDATE:
      return { ...storeState, queryValidationMessage: action.queryValidationMessage }
  }

  return storeState;
}