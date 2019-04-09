import { AppAction } from '../actions';
import { StoreState } from '../types';
import { ACCESS_TOKEN_UPDATE } from '../constants';

export function reducer(storeState: StoreState, action: AppAction): StoreState {
  switch (action.type) {
    case ACCESS_TOKEN_UPDATE:
      const accessToken = action.accessToken;
      return {...storeState, accessToken: accessToken };
  }

  return storeState;
}