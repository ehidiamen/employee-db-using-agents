/// <reference types="react-scripts" />

declare namespace NodeJS {
  interface ProcessEnv {
    /**
     * Origin of the Java Employee API.
     * Set the value in `.env.development` (not in this file).
     * Example: http://localhost:8080
     */
    readonly REACT_APP_API_BASE_URL?: string;
  }
}
