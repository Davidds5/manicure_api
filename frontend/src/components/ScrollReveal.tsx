"use client";

import React, { useEffect, useRef, useState } from "react";

interface ScrollRevealProps extends React.HTMLAttributes<HTMLDivElement> {
  children: React.ReactNode;
  className?: string;
  delay?: number; // Delay em ms (para efeito cascata/stagger suave)
  offset?: number; // Deslocamento vertical em px (padrão: 16px)
  duration?: number; // Duração em ms (padrão: 400ms, suave e fluido)
}

/**
 * ScrollReveal - Animação de entrada ultra-fluida acelerada por GPU.
 * 
 * Vantagens técnicas:
 * 1. ZERO KB de biblioteca externa: utiliza a API nativa IntersectionObserver.
 * 2. 60 FPS garantidos em mobile: renderizado na GPU via translate3d e opacity.
 * 3. Acessibilidade nativa: respeita prefers-reduced-motion do usuário.
 * 4. Stagger/Cascata suave com curva cúbica natural cubic-bezier(0.16, 1, 0.3, 1).
 */
export function ScrollReveal({
  children,
  className = "",
  delay = 0,
  offset = 16,
  duration = 400,
  style,
  ...rest
}: ScrollRevealProps) {
  const [isVisible, setIsVisible] = useState(false);
  const ref = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const el = ref.current;
    if (!el) return;

    if (typeof window !== "undefined" && window.matchMedia("(prefers-reduced-motion: reduce)").matches) {
      setIsVisible(true);
      return;
    }

    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          setIsVisible(true);
          observer.unobserve(entry.target);
        }
      },
      {
        threshold: 0.08,
        rootMargin: "0px 0px -30px 0px",
      }
    );

    observer.observe(el);

    return () => {
      observer.disconnect();
    };
  }, []);

  return (
    <div
      ref={ref}
      style={{
        ...style,
        transitionDuration: `${duration}ms`,
        transitionDelay: `${delay}ms`,
        transform: isVisible ? "translate3d(0, 0, 0)" : `translate3d(0, ${offset}px, 0)`,
      }}
      className={[
        "transition-all ease-[cubic-bezier(0.16,1,0.3,1)] will-change-[transform,opacity]",
        isVisible ? "opacity-100" : "opacity-0 pointer-events-none",
        className,
      ].join(" ")}
      {...rest}
    >
      {children}
    </div>
  );
}
