import React, { useState, useEffect, useRef } from "react";
            import { AllCommunityModule, ModuleRegistry } from "ag-grid-community";
            import { AgGridReact } from "ag-grid-react";
            import { getStockUpdates } from './StockDataService';

            ModuleRegistry.registerModules([AllCommunityModule]);

            function App() {
              const [stocksMap, setStocksMap] = useState({});
              const gridRef = useRef(null);

              const columnDefs = [
                { headerName: 'Symbol', field: 'symbol' },
                { headerName: 'Price', field: 'price', valueFormatter: params => params.value ? params.value.toFixed(2) : '' },
                { headerName: 'Timestamp', field: 'timestamp', valueFormatter: params => {
                  return params.value ? new Date(params.value * 1000).toLocaleTimeString() : '';
                }}
              ];

              useEffect(() => {
                const stream = getStockUpdates('AAPL', (update) => {
                  setStocksMap(prevStocks => ({
                    ...prevStocks,
                    [update.symbol]: update // Update the stock with the latest data
                  }));
                });

                return () => {
                  if (stream && stream.cancel) {
                    stream.cancel();
                  }
                };
              }, []);

              // Convert stocks map to array for AG Grid
              const rowData = Object.values(stocksMap);

              return (
                <div className="ag-theme-alpine" style={{ height: '400px', width: '600px' }}>
                  <AgGridReact
                    ref={gridRef}
                    columnDefs={columnDefs}
                    rowData={rowData}
                    getRowId={params => params.data.symbol} // Key rows by symbol
                  />
                </div>
              );
            }

            export default App;
