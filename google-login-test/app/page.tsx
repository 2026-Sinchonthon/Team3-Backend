'use client';

import Script from 'next/script';
import { useRef, useState } from 'react';

type Credential = { credential: string };
type LoginResult = { status: number; body: unknown };

declare global {
  interface Window {
    google?: { accounts: { id: {
      initialize(config: { client_id: string; callback: (response: Credential) => void }): void;
      renderButton(element: HTMLElement, options: Record<string, unknown>): void;
    } } };
  }
}

export default function Home() {
  const buttonRef = useRef<HTMLDivElement>(null);
  const [clientId, setClientId] = useState('');
  const [status, setStatus] = useState('OAuth 클라이언트 ID를 입력해주세요.');
  const [result, setResult] = useState<LoginResult | null>(null);

  function prepareGoogleLogin() {
    const id = clientId.trim();
    if (!id) return setStatus('OAuth 클라이언트 ID를 먼저 입력해주세요.');
    if (!window.google || !buttonRef.current) {
      return setStatus('Google 스크립트를 불러오는 중입니다. 잠시 후 다시 눌러주세요.');
    }
    buttonRef.current.replaceChildren();
    window.google.accounts.id.initialize({ client_id: id, callback: sendToken });
    window.google.accounts.id.renderButton(buttonRef.current, {
      type: 'standard', theme: 'outline', size: 'large', text: 'signin_with', width: 320,
    });
    setStatus('아래 Google 버튼을 눌러 로그인하세요.');
  }

  async function sendToken(response: Credential) {
    setStatus('Google ID Token을 백엔드에서 검증하고 있습니다…');
    setResult(null);
    try {
      const res = await fetch('http://localhost:8080/api/v1/auth/oauth/google', {
        method: 'POST', credentials: 'include',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ idToken: response.credential }),
      });
      const body = await res.json().catch(() => null);
      setResult({ status: res.status, body });
      setStatus(res.ok ? '로그인 성공 — 사용자가 DB에 저장됐습니다.' : '로그인 실패 — 아래 응답을 확인해주세요.');
    } catch {
      setStatus('백엔드에 연결할 수 없습니다. localhost:8080 실행 상태를 확인해주세요.');
    }
  }

  return (
    <main className="page-shell">
      <Script src="https://accounts.google.com/gsi/client" strategy="afterInteractive" />
      <section className="login-card">
        <div className="eyebrow">Team 3 · Local auth check</div>
        <h1>Google 로그인 테스트</h1>
        <p className="intro">실제 Google ID Token을 백엔드로 전송해 사용자 생성과 JWT 발급을 확인합니다.</p>
        <label htmlFor="client-id">Google OAuth 2.0 클라이언트 ID</label>
        <input id="client-id" value={clientId} onChange={(e) => setClientId(e.target.value)}
          placeholder="000000000000-xxxx.apps.googleusercontent.com" autoComplete="off" />
        <button className="prepare-button" type="button" onClick={prepareGoogleLogin}>Google 로그인 준비</button>
        <div className="google-button" ref={buttonRef} />
        <div className="status" role="status" aria-live="polite"><span className="status-dot" />{status}</div>
        {result && <section className="result-panel">
          <div className="result-heading"><h2>백엔드 응답</h2>
            <span className={result.status < 400 ? 'success-code' : 'error-code'}>HTTP {result.status}</span>
          </div>
          <pre>{JSON.stringify(result.body, null, 2)}</pre>
        </section>}
      </section>
    </main>
  );
}
