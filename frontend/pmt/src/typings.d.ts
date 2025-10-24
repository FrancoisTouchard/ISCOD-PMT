// Déclarations de types des composants bootstrap
declare module 'bootstrap' {
  export class Toast {
    constructor(
      element: HTMLElement,
      options?: { delay?: number; autohide?: boolean }
    );
    show(): void;
    hide(): void;
    dispose(): void;
  }
}
